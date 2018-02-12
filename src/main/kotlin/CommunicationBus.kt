import java.lang.ref.WeakReference
import java.util.ArrayList

class CommunicationBus {
    private val registeredEventListeners = ArrayList<Triple<Class<*>, WeakReference<Any>, (Any) -> Unit>>(100)

    fun <T : Any> post(eventClass: T) {
        cleanup()
        registeredEventListeners.filter { it.first == eventClass.javaClass }.forEach { it.third(eventClass) }
    }

    fun <T : Any> expect(eventClass: Class<T>, receiverObject: Any, listeningCallback: (T) -> Unit) {
        cleanup()
        registeredEventListeners.add(Triple(eventClass, WeakReference(receiverObject), listeningCallback as (Any) -> Unit))
    }

    private fun cleanup() {
        registeredEventListeners.removeAll { it.second.get() == null }
    }

    companion object {
        val instance = CommunicationBus()
    }
}
