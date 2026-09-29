@file:Suppress("Unused")

package foo.starred.kbus.extensions

import foo.starred.kbus.impl.KBus
import foo.starred.kbus.base.IReactiveProperty
import foo.starred.kbus.data.event.base.KBusEvent
import foo.starred.kbus.data.event.traits.KBusUnconditionalTrait
import foo.starred.kbus.data.node.KBusNode

inline fun <reified T : KBusEvent> on(
    priority: Int = 0,
    bus: KBus,
    noinline handler: T.() -> Unit
) = KBusNode(T::class.java, handler, priority, bus).apply { register() }

inline fun <reified T : KBusEvent> KBus.on(
    priority: Int = 0,
    noinline handler: T.() -> Unit
) = on(priority, this, handler)

fun KBusNode<*>.runWhen(condition: IReactiveProperty<Boolean>) = apply {
    if (overridden) return@apply
    if (KBusUnconditionalTrait::class.java.isAssignableFrom(klass)) return@apply

    add(condition)
}

fun KBusNode<*>.override(condition: IReactiveProperty<Boolean>) = apply {
    overridden = true
    conditions.clear()
    add(condition)
}

fun KBusNode<*>.override() = apply {
    overridden = true
    conditions.clear()
    register()
}

private fun KBusNode<*>.add(condition: IReactiveProperty<Boolean>) = apply {
    conditions.add(condition)
    condition.observe { evaluate() }
    evaluate()
}
