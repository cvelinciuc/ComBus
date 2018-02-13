package com.vcristian.combus

import com.vcristian.combus.CommunicationBus.Companion.instance as ComBus


/**
 * Subscribes the referenced object to executing a [listeningCallback] function
 * upon receiving some [eventClass] objects and adds this subscription
 * to the registry of shared instance of [CommunicationBus]

 * @author Cristian Velinciuc
 *
 * @exception UnsupportedOperationException thrown when an instance of CommunicationBus is transmitted as [receiverObject]
 *
 * @param eventClass expected [T] type of object should trigger this listener
 * @param listeningCallback a function to be triggered when a [T] type of object is received
 */
fun <T: Any> Any.expect(eventClass: Class<T>, listeningCallback: (T) -> Unit) {
  ComBus.expect(eventClass, this, listeningCallback)
}

/**
 * Emits the given [eventObject] to all the available listeners available in the
 * registry of the shared instance of [CommunicationBus]
 *
 * @author Cristian Velinciuc
 *
 * @param eventObject an instance of some [T] class that some listener is potentially expecting to receive
 */
fun <T: Any> post(eventObject: T) {
  ComBus.post(eventObject)
}