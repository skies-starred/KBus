@file:Suppress("Unused")

package foo.starred.kbus.data.event.traits

import foo.starred.kbus.data.event.base.KBusEvent

interface KBusCancellableTrait {
    fun cancel() {
        (this as KBusEvent).cancelled = true
    }
}
