package io.github.takenoko4096.starlight.math

interface IVector<T : IVector<T, U>, U : Number> {
    val isZero: Boolean

    override fun hashCode(): Int

    override operator fun equals(other: Any?): Boolean

    fun components(): List<U>

    @Destructive
    fun calculate(operator: (U) -> U): T

    @Destructive
    fun calculate(other: T, operator: (U, U) -> U): T

    @Destructive
    fun calculate(other1: T, other2: T, operator: (U, U, U) -> U): T

    @Destructive
    infix fun add(other: T): T

    @Destructive
    infix fun subtract(other: T): T

    @Destructive
    infix fun scale(scalar: U): T

    @Destructive
    fun invert(): T

    @Destructive
    fun clamp(min: T, max: T): T

    override fun toString(): String

    fun copy(): T
}
