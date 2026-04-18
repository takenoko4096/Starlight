package io.github.takenoko4096.starlight.event

abstract class CancellableEvent protected constructor() : Event() {
    var isCancelled: Boolean = false
}

