package com.vcristian.combus

import org.junit.Test
import com.vcristian.combus.CommunicationBus.Companion.instance as ComBus


class CommunicationBusTests {
  inner class Emittable(val emittedValue: Int)
  inner class Receiver(var value: Int)
  inner class Emitter() {
    fun emitEvent(emittedValue: Int) {
      ComBus.post(Emittable(emittedValue))
    }
  }

  @Test
  fun `successful communication between existing objects`() {
    val receiversCount = 10

    val eventReceivers = Array<Receiver?>(receiversCount, { Receiver(0) })
    val eventEmitter   = Emitter()

    val expectedValuesAfterFirstPass  = receiversCount * receiversCount
    val expectedValuesAfterSecondPass = receiversCount * receiversCount * receiversCount

    eventReceivers.forEach { eventReceiver ->
      eventReceiver?.let {
        ComBus.expect(Emittable::class.java, eventReceiver) { receivedEmittable ->
          eventReceiver.value = receivedEmittable.emittedValue
        }
      }
    }

    expect(String::class.java) { receivedItem ->
      println(receivedItem)
    }

    eventReceivers.indices.forEach { index ->
      eventEmitter.emitEvent((index + 1) * (index + 1))
    }

    val firstPassResults = eventReceivers.map { it?.value ?: -1 }

    assert(firstPassResults.all { it == expectedValuesAfterFirstPass })

    (0 until eventReceivers.size / 2).forEach { index ->
      eventReceivers[index] = null
    }

    System.gc()

    eventReceivers.indices.forEach { index ->
      eventEmitter.emitEvent((index + 1) * (index + 1) * (index + 1))
    }

    val secondPassFirstHalfResults  = eventReceivers.slice(0 until receiversCount / 2).map { it?.value ?: -1 }
    val secondPassSecondHalfResults = eventReceivers.slice(receiversCount / 2 until receiversCount).map { it?.value ?: -1 }

    assert(secondPassFirstHalfResults.all { it == -1 })
    assert(secondPassSecondHalfResults.all { it == expectedValuesAfterSecondPass })
  }
}