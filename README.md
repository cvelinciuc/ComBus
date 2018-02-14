# ComBus
Inter-object, event-driven, communication framework.

[![Release](https://jitpack.io/v/msacras/ComBus.svg)](https://jitpack.io/#msacras/ComBus)

ComBus aims to provide a simple mean for objects to communicate data. It is supposed to be an effortless mechanism of triggering custom events and reacting to them. This was designed to be a lightweight alternative to Rx frameworks with no extra dependencies.

![ComBus icon](/combus_icon.png)

### What's [new](/CHANGELOG.md) in 1.2

 * Listener removal feature
 * Concurrent registry modification

### Initialize ComBus
Initialization is as simple as creation of an instance of the _CommunicationBus_ class:

```
val eventBus = CommunicationBus()
```

If needed, a global _"shared" instance_ is always available as a static field of _CommunicationBus_ class, and can be accessed the following way:

```
CommunicationBus.instance
```

Or imported accordingly, with an alias for convenience:

```
import com.vcristian.combus.CommunicationBus.Companion.instance as ComBus
```

### Adding a listener
Registering a listener to an event takes as little as invocation of the _expect_ method of an instance of _CommunicationBus_ class, given the following arguments:
  * eventClass: Class<T\> - the event class, meant to indicate the expected type of object should trigger this listener
  * receiverObject: Any - reference to the object which the listener will be bound to
  * listeningCallback: (T) -> Unit - callback function which gets invoked as soon as a Class<T\> type of event is emitted

If the _receiverObject_ gets collected and is no longer available, _listeningCallback_ is automatically unregistered.

Usage example:
```
comBusInstance.expect(SomeClass::class.java, this) { receivedItem ->
  println(receivedItem)
}
```
There is also an extension function that allows invocation of this method from an instance of _Any_ type of object, rather than referencing it as a receiver argument. When used this way, the listener gets added into the listeners registry of _"shared" instance_ of _CommunicationBus_.

**Note:** An instance of _CommunicationBus_ cannot also be an event receiver, and thus, invoking _expect_ with such and object as a _receiverObject_ argument will throw an _UnsupportedOperationException_.

Usage example:
```
someObjectInstance.expect(SomeClass::class.java) { receivedItem ->
  println(receivedItem)
}
```

### Triggering an event
In order to emit an event to all the potential listeners, it is only necesary to invoke the _post_ method, with the following arguments:
 * eventObject: T - an instance of some _<T\>_ class that some listener is potentially expecting to receive.

Usage example:
```
comBusInstance.post(SomeClass())
```
When invoked, the _eventObject_ object will immediately be delivered to all the listeners that have been registered for the _<T>_ type of objects **on the same instance of CommuncationBus**.

There is also an extension function that allows invocation of this method without an instance of _CommunicationBus_. This implies that the event will be delivered to listeners available in the registry of the _"shared" instance_ of _CommunicationBus_ only.

Usage example:
```
post(SomeClass())
```

### Removing a listener
To stop an object's event listener from receiving new events, it is sufficient to call the _dismiss_ method with the following arguments:
 * eventClass: Class<T\> - the event class, meant to indicate the type of object that potentially triggers object's event listener
 * receiverObject: Any - reference to the object which that potentially has a listener bound it

Usage example:
```
comBusInstance.dismiss(SomeClass::java.class, someObjectInstance)
```
When invoked, if a listener matching provided criteria exists, the callback function will immediately be removed from the registry **of the same instance of CommuncationBus**.

An extension function is also available to ease the invokation of this method without an instance of _CommunicationBus_, and thus - removal of a listener from the registry of the _"shared" instance_ of _CommunicationBus_. 

**Note:** An instance of _CommunicationBus_ cannot also be an event receiver, and thus, invoking _expect_ with such and object as a _receiverObject_ argument will throw an _UnsupportedOperationException_.

Usage example:
```
someObjectInstance.dismiss(SomeClass::java.class)
```

### Add dependency
To get ComBus into your project:

**Step 1**. Add the JitPack repository to your build.gradle file
Add it in your root build.gradle at the end of repositories:
```
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```
**Step 2**. Add the dependency
```
dependencies {
  compile 'com.github.User:Repo:Tag'
}
```

### TODO
- [x] A preliminary (POC) implementation of the _Event Bus_ mechanism
- [x] Listener removal feature
- [x] Concurrent listeners registry modification
- [ ] Cover whole library with proper tests
- [ ] Extra features?

### License
```
MIT License

Copyright (c) 2018 Cristian Velinciuc

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
