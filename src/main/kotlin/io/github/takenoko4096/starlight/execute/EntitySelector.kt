package io.github.takenoko4096.starlight.execute

import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.GameType

class EntitySelector(callback: EntitySelector.() -> Unit) {
    private val types = mutableSetOf<InvertibleValue<EntityType<*>>>()
    private val names = mutableSetOf<InvertibleValue<String>>()
    private val gameModes = mutableSetOf<InvertibleValue<GameType>>()
    private val tags = mutableSetOf<InvertibleValue<String>>()

    var x: Double? = null
    var y: Double? = null
    var z: Double? = null

    var distance: ClosedFloatingPointRange<Double>? = null

    var yRotation: ClosedFloatingPointRange<Float>? = null
    var xRotation: ClosedFloatingPointRange<Float>? = null

    var dx: Double? = null
    var dy: Double? = null
    var dz: Double? = null

    var limit: Int? = null

    var team: String? = null

    var sort: SelectorSortOrder = SelectorSortOrder.ARBITRARY

    var type: Any
        get() {
            throw IllegalSelectorArgumentException("このプロパティは getter が無効です")
        }
        set(value) {
            when (value) {
                is Not<*> if value.value is EntityType<*> -> {
                    if (types.any { !it.not }) {
                        throw IllegalSelectorArgumentException("この引数は既に単一のエンティティタイプが指定されています")
                    }

                    types.add(InvertibleValue.of(value.value))
                }
                is EntityType<*> -> {
                    types.add(InvertibleValue.of(value))
                }
                else -> {
                    throw IllegalSelectorArgumentException("セレクタ引数 'type' に対して無効な型です: ${value::class.qualifiedName}")
                }
            }
        }

    var name: Any
        get() {
            throw IllegalSelectorArgumentException("このプロパティは getter が無効です")
        }
        set(value) {
            when (value) {
                is Not<*> if value.value is String -> {
                    if (names.any { !it.not }) {
                        throw IllegalSelectorArgumentException("この引数は既に単一の名前が指定されています")
                    }

                    names.add(InvertibleValue.of(value.value))
                }
                is String -> {
                    names.add(InvertibleValue.of(value))
                }
                else -> {
                    throw IllegalSelectorArgumentException("セレクタ引数 'name' に対して無効な型です: ${value::class.qualifiedName}")
                }
            }
        }

    var gameMode: Any
        get() {
            throw IllegalSelectorArgumentException("このプロパティは getter が無効です")
        }
        set(value) {
            when (value) {
                is Not<*> if value.value is GameType -> {
                    if (names.any { !it.not }) {
                        throw IllegalSelectorArgumentException("この引数は既に単一のゲームモードが指定されています")
                    }

                    gameModes.add(InvertibleValue.of(value.value))
                }
                is GameType -> {
                    gameModes.add(InvertibleValue.of(value))
                }
                else -> {
                    throw IllegalSelectorArgumentException("セレクタ引数 'gameMode' に対して無効な型です: ${value::class.qualifiedName}")
                }
            }
        }

    var tag: Any
        get() {
            throw IllegalSelectorArgumentException("このプロパティは getter が無効です")
        }
        set(value) {
            when (value) {
                is Not<*> if value.value is String -> {
                    tags.add(InvertibleValue.of(value.value))
                }
                is String -> {
                    tags.add(InvertibleValue.of(value))
                }
                else -> {
                    throw IllegalSelectorArgumentException("セレクタ引数 'tag' に対して無効な型です: ${value::class.qualifiedName}")
                }
            }
        }

    operator fun String.not(): Not<String> {
        return Not(this)
    }

    operator fun EntityType<*>.not(): Not<EntityType<*>> {
        return Not(this)
    }

    init {
        callback()
    }

    class Not<T : Any> internal constructor(internal val value: T)

    private class IllegalSelectorArgumentException(message: String) : RuntimeException(message)
}
