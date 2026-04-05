package io.github.takenoko4096.starlight.util.nbt

class NbtSerializationException(message: String, cause: Throwable?) : RuntimeException(message, cause) {
    constructor(message: String) : this(message, null)
}
