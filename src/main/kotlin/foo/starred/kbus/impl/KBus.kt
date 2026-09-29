@file:Suppress("UNCHECKED_CAST", "Unused")

package foo.starred.kbus.impl

import foo.starred.kbus.data.event.base.KBusEvent
import foo.starred.kbus.data.node.KBusNode
import java.util.concurrent.ConcurrentHashMap

open class KBus {
    val all = ConcurrentHashMap<Class<out KBusEvent>, Array<KBusNode<out KBusEvent>>>()

    fun <T : KBusEvent> post(event: T) {
        val nodes = all[event.javaClass] as? Array<KBusNode<T>> ?: return
        for (i in nodes) i.handler(event)
    }

    fun add(node: KBusNode<*>) {
        val a = node.klass

        while (true) {
            val old = all[a]
            val size = old?.size ?: 0

            if (old?.any { it === node } == true) return
            val new = arrayOfNulls<KBusNode<*>>(size + 1) as Array<KBusNode<*>>
            var i = 0

            old?.let {
                while (i < size && old[i].priority <= node.priority) new[i] = old[i].also { i++ }
                if (i < size) System.arraycopy(old, i, new, i + 1, size - i)
            }

            new[i] = node
            if (old == null && all.putIfAbsent(a, new) == null || old != null && all.replace(a, old, new)) return
        }
    }

    fun remove(node: KBusNode<*>) {
        val a = node.klass

        while (true) {
            val old = all[a] ?: return
            val size = old.size
            val index = old.indexOfFirst { it === node }
            if (index == -1) return

            if (size == 1) {
                if (all.remove(a, old)) return
                continue
            }

            val new = arrayOfNulls<KBusNode<*>>(size - 1) as Array<KBusNode<*>>
            if (index > 0) System.arraycopy(old, 0, new, 0, index)
            if (index < size - 1) System.arraycopy(old, index + 1, new, index, size - index - 1)

            if (all.replace(a, old, new)) return
        }
    }
}
