package io.github.takenoko4096.starlight.nbt

class NbtSerializationException internal constructor(message: String, cause: Throwable?) : RuntimeException(message, cause) {
    internal constructor(message: String) : this(message, null)
}
