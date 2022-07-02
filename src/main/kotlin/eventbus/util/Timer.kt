package eventbus.util

import eventbus.domain.Timestamp

object Timer {
    fun getTime() = Timestamp(System.nanoTime())
}