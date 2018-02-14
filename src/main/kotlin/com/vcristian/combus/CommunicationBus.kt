package com.vcristian.combus

import java.lang.ref.WeakReference
import java.util.Collections

class CommunicationBus {
  /**
   * Registry of event listeners. An array containing items that have:
   * types of receivable objects [Class],
   * [WeakReference] references to the event-receiving objects,
   * [(Any) -> Unit] callback function to be triggered on event
   *
   * @author Cristian Velinciuc
   */
  private val eventListenersRegistry = Collections.synchronizedList(mutableListOf<Triple<Class<*>, WeakReference<Any>, (Any) -> Unit>>())

  /**
   * Subscribes some [receiverObject] to executing a [listeningCallback] function
   * upon receiving some [eventClass] objects and adds this subscription
   * to the registry of invoked instance of [CommunicationBus]

   * @author Cristian Velinciuc
   *
   * @exception UnsupportedOperationException thrown when an instance of CommunicationBus is transmitted as [receiverObject]
   *
   * @param eventClass expected [T] type of object should trigger this listener
   * @param receiverObject the object that this listener will be bound to
   * @param listeningCallback a function to be triggered when a [T] type of object is received
   */
  @Synchronized
  fun <T: Any> expect(eventClass: Class<T>, receiverObject: Any, listeningCallback: (T) -> Unit) {
    if (receiverObject is CommunicationBus) throw UnsupportedOperationException("InvalidReceiverException")

    dismiss(eventClass, receiverObject)
    eventListenersRegistry.add(Triple(eventClass, WeakReference(receiverObject), listeningCallback as (Any) -> Unit))
  }

  /**
   * Unsubscribes some [receiverObject] from receiving some [eventClass] objects and removes
   * this subscription from the registry of invoked instance of [CommunicationBus]
   *
   * @author Cristian Velinciuc
   *
   * @param eventClass [T] type of object that potentially triggers this [receiverObject]'s listener
   * @param receiverObject the object that potentially has a listener bound it
   */
  @Synchronized
  fun <T: Any> dismiss(eventClass: Class<T>, receiverObject: Any) {
    if (receiverObject is CommunicationBus) throw UnsupportedOperationException("InvalidReceiverException")

    eventListenersRegistry.removeAll { it.first == eventClass && it.second.get() == receiverObject }
  }

  /**
   * Emits the given [eventObject] to all the available listeners available in the
   * registry of invoked instance of [CommunicationBus]
   *
   * @author Cristian Velinciuc
   *
   * @param eventObject an instance of some [T] class that some listener is potentially expecting to receive
   */
  @Synchronized
  fun <T: Any> post(eventObject: T) {
    cleanup()
    eventListenersRegistry.filter { it.first == eventObject.javaClass }.forEach { it.third(eventObject) }
  }

  /**
   * Removes from registry those listener whose receiver objects have been collected and have a null reference
   *
   * @author Cristian Velinciuc
   */
  private fun cleanup() {
    eventListenersRegistry.removeAll { it.second.get() == null }
  }

  companion object {
    /**
     * A static instance of CommunicationBus class, meant to server as shared instance with global scope
     *
     * @author Cristian Velinciuc
     */
    val instance = CommunicationBus()
  }
}
