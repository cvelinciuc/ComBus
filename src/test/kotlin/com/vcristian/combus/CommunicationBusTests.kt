package com.vcristian.combus

import org.junit.Test
import kotlin.concurrent.thread


class CommunicationBusTests {
  inner class Emittable(val emittedValue: Int)
  inner class Receiver(var value: Int)
  inner class Emitter {
    fun emitEvent(emittedValue: Int) {
      post(Emittable(emittedValue))
    }
  }

  @Test
  fun `successful communication between existing objects`() {
    val receiversCount = 10

    val eventReceivers = Array<Receiver?>(receiversCount, { Receiver(0) })
    val eventEmitter = Emitter()

    val expectedValuesAfterFirstPass = receiversCount * receiversCount
    val expectedValuesAfterSecondPass = receiversCount * receiversCount * receiversCount

    eventReceivers.forEach { eventReceiver ->
      eventReceiver?.let {
        eventReceiver.expect(Emittable::class.java) { receivedEmittable ->
          eventReceiver.value = receivedEmittable.emittedValue
        }
      }
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

    val secondPassFirstHalfResults = eventReceivers.slice(0 until receiversCount / 2).map { it?.value ?: -1 }
    val secondPassSecondHalfResults = eventReceivers.slice(receiversCount / 2 until receiversCount).map {
      it?.value ?: -1
    }

    assert(secondPassFirstHalfResults.all { it == -1 })
    assert(secondPassSecondHalfResults.all { it == expectedValuesAfterSecondPass })
  }

  @Test
  fun `removing event listeners`() {
    val receiversCount = 10
    val eventReceivers = Array<Receiver?>(receiversCount, { Receiver(0) })

    eventReceivers.forEach { eventReceiver ->
      eventReceiver?.let {
        eventReceiver.expect(Emittable::class.java) { receivedEmittable ->
          eventReceiver.value = receivedEmittable.emittedValue
        }
      }
    }

    eventReceivers.forEachIndexed { index, eventReceiver ->
      post(Emittable(index * index))
      eventReceiver?.dismiss(Emittable::class.java)
    }

    val actualResults = eventReceivers.map { eventReceiver -> eventReceiver?.value }
    val expectedResults = List(receiversCount, { index -> index * index })

    assert(eventReceivers.indices.all { index -> actualResults[index] == expectedResults[index] })
  }

  @Test
  fun `concurrent modification`() {
    val receiversCount = 1000
    val eventReceivers = Array<Receiver?>(receiversCount, { Receiver(0) })
    var success = true

    val subscriberThread = thread {
      try {
        eventReceivers.forEachIndexed { index, eventReceiver ->
          eventReceiver?.expect(Emittable::class.java) {}
        }
      } catch (e: Exception) {
        e.printStackTrace()
        success = false
      }
    }

    val unsubscriberThread = thread {
      try {
        eventReceivers.forEachIndexed { index, eventReceiver ->
          eventReceiver?.dismiss(Emittable::class.java)
        }
      } catch (e: Exception) {
        e.printStackTrace()
        success = false
      }
    }

    val emitterThread = thread {
      try {
        eventReceivers.forEachIndexed { index, eventReceiver ->
          post(Emittable(index))
        }
      } catch (e: Exception) {
        e.printStackTrace()
        success = false
      }
    }

    subscriberThread.join()
    unsubscriberThread.join()
    emitterThread.join()

    assert(success)
  }
}