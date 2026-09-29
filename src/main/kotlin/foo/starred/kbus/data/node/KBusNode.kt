@file:Suppress("Unused")

package foo.starred.kbus.data.node

import foo.starred.kbus.impl.KBus
import foo.starred.kbus.base.IReactiveProperty
import foo.starred.kbus.data.event.base.KBusEvent
import java.util.concurrent.atomic.AtomicBoolean

open class KBusNode<T : KBusEvent>(
    val klass: Class<T>,
    var handler: (T) -> Unit,
    val priority: Int = 0,
    val bus: KBus
) {
    private val state = AtomicBoolean(false)

    val conditions = mutableListOf<IReactiveProperty<Boolean>>()
    var overridden = false

    fun once() = apply {
        val original = handler
        handler = {
            original(it)
            unregister()
        }
    }

    fun register(): Boolean {
        if (!state.compareAndSet(false, true)) return false
        bus.add(this)
        return true
    }

    fun unregister(): Boolean {
        if (!state.compareAndSet(true, false)) return false
        bus.remove(this)
        return true
    }

    fun evaluate() {
        if (conditions.all { it.value }) register() else unregister()
    }
}
