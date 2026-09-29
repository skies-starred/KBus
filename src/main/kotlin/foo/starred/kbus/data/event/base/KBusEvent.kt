@file:Suppress("Unused")

package foo.starred.kbus.data.event.base

import foo.starred.kbus.impl.KBus

abstract class KBusEvent {
    var cancelled: Boolean = false

    fun post(bus: KBus): Boolean {
        bus.post(this)
        return cancelled
    }
}
