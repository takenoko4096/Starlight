package io.github.takenoko4096.starlight.util.nbt

import com.gmail.takenokoii78.mojangson.values.MojangsonByteArray
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound
import com.gmail.takenokoii78.mojangson.values.MojangsonIntArray
import com.gmail.takenokoii78.mojangson.values.MojangsonIterable
import com.gmail.takenokoii78.mojangson.values.MojangsonLongArray
import com.gmail.takenokoii78.mojangson.values.MojangsonPrimitive
import com.gmail.takenokoii78.mojangson.values.MojangsonStructure
import io.github.takenoko4096.starlight.util.text.VanillaColor
import io.github.takenoko4096.starlight.util.text.component
import net.minecraft.network.chat.Component
import kotlin.charArrayOf
import kotlin.reflect.KClass
import kotlin.text.contains
import kotlin.text.repeat

class NbtSerializer private constructor(
    private val value: MojangsonStructure,
    private val indentationSpaceCount: Int
) {
    private fun serialize(): Component {
        return serialize(value, 1)
    }

    private fun serialize(value: Any?, indentation: Int): Component {
        return when (value) {
            null -> component {
                textColor(VanillaColor.LIGHT_PURPLE)
                text("null")
            }
            is Number -> number(value)
            is String -> string(value)
            is MojangsonCompound -> compound(value, indentation)
            is MojangsonIterable<*> -> iterable(value, indentation)
            is MojangsonPrimitive<*> -> serialize(value.value, indentation)
            else -> throw NbtSerializationException("このオブジェクトは無効な型の値を含みます")
        }
    }

    private fun compound(compound: MojangsonCompound, indentation: Int): Component {
        val keys = compound.keys().toTypedArray()
        return component {
            textColor(VanillaColor.WHITE)
            text(COMPOUND_BRACES[0])

            section {
                for (i in keys.indices) {
                    val key = keys[i]

                    try {
                        val childValue: Any = compound.get(key, compound.getTypeOf(key))

                        text(LINE_BREAK)
                        text(indentation(indentation + 1))

                        section {
                            textColor(VanillaColor.AQUA)
                            component(key(key))
                        }

                        text(COLON)
                        text(WHITESPACE)
                        component(serialize(childValue, indentation + 1))
                    }
                    catch (e: IllegalArgumentException) {
                        throw NbtSerializationException(
                            "キー '$key' における無効な型: ${compound.getTypeOf(key)}",
                            e
                        )
                    }

                    if (i != keys.size - 1) {
                        text(COMMA)
                    }
                }

                if (keys.isNotEmpty()) {
                    text(LINE_BREAK)
                    text(indentation(indentation))
                }
            }

            text(COMPOUND_BRACES[1])
        }
    }

    private fun iterable(iterable: MojangsonIterable<*>, indentation: Int): Component {
        return component {
            textColor(VanillaColor.WHITE)
            text(ARRAY_LIST_BRACES[0])

            section {
                if (ITERABLE_TYPE_SYMBOLS.containsKey(iterable::class)) {
                    section {
                        textColor(VanillaColor.RED)
                        text(ITERABLE_TYPE_SYMBOLS[iterable::class]!!)
                    }
                    text(SEMICOLON)
                }

                for ((i, element) in iterable.withIndex()) {
                    if (i >= 1) {
                        text(COMMA)
                    }

                    try {
                        text(LINE_BREAK)
                        text(indentation(indentation + 1))
                        component(serialize(element, indentation + 1))
                    }
                    catch (e: IllegalArgumentException) {
                        throw NbtSerializationException(
                            "インデックス'" + i + "における無効な型: ${element.javaClass.name}", e
                        )
                    }

                }

                if (!iterable.isEmpty) {
                    text(LINE_BREAK)
                    text(indentation(indentation))
                }
            }

            text(ARRAY_LIST_BRACES[1])
        }
    }

    private fun key(value: String): Component {
        val quote = if (value.contains(DOUBLE_QUOTE)) SINGLE_QUOTE else DOUBLE_QUOTE
        val requireQuote = SYMBOLS_ON_QUOTED[quote]!!.any { value.contains(it) }

        return component {
            if (requireQuote) {
                text(quote)
            }

            section {
                textColor(VanillaColor.AQUA)
                text(value)
            }

            if (requireQuote) {
                text(quote)
            }
        }
    }

    private fun string(value: String): Component {
        val quote = if (value.contains(DOUBLE_QUOTE)) SINGLE_QUOTE else DOUBLE_QUOTE
        val requireQuote = SYMBOLS_ON_QUOTED[quote]!!.any { value.contains(it) }

        return component {
            if (requireQuote) {
                text(quote)
            }

            section {
                textColor(VanillaColor.GREEN)
                text(value)
            }

            if (requireQuote) {
                text(quote)
            }
        }
    }

    private fun number(value: Number): Component {
        return component {
            textColor(VanillaColor.GOLD)
            text(value.toString())

            if (NUMBER_TYPE_SYMBOLS.contains(value::class)) {
                textColor(VanillaColor.RED)
                text(NUMBER_TYPE_SYMBOLS[value::class]!!)
            }
        }
    }

    private fun indentation(indentation: Int): String {
        return WHITESPACE.toString().repeat(this.indentationSpaceCount).repeat(indentation - 1)
    }

    companion object {
        private const val LINE_BREAK = '\n'
        private const val SINGLE_QUOTE = '\''
        private const val DOUBLE_QUOTE = '"'
        private const val COLON = ':'
        private const val COMMA = ','
        private const val SEMICOLON = ';'
        private val COMPOUND_BRACES = charArrayOf('{', '}')
        private val ARRAY_LIST_BRACES = charArrayOf('[', ']')
        private const val WHITESPACE = ' '
        private const val ESCAPE = '\\'

        private val SYMBOLS_ON_QUOTED = mapOf(
            SINGLE_QUOTE to setOf(
                COLON, COMMA, SEMICOLON,
                COMPOUND_BRACES[0], COMPOUND_BRACES[1],
                ARRAY_LIST_BRACES[0], ARRAY_LIST_BRACES[1],
                WHITESPACE, LINE_BREAK, ESCAPE,
                SINGLE_QUOTE
            ),
            DOUBLE_QUOTE to setOf(
                COLON, COMMA, SEMICOLON,
                COMPOUND_BRACES[0], COMPOUND_BRACES[1],
                ARRAY_LIST_BRACES[0], ARRAY_LIST_BRACES[1],
                WHITESPACE, LINE_BREAK, ESCAPE,
                DOUBLE_QUOTE
            )
        )

        private val ITERABLE_TYPE_SYMBOLS = mapOf<KClass<out MojangsonIterable<*>>, Char>(
            MojangsonByteArray::class to 'B',
            MojangsonIntArray::class to 'I',
            MojangsonLongArray::class to 'L'
        )

        private val NUMBER_TYPE_SYMBOLS = mapOf<KClass<out Number>, Char>(
            Byte::class to 'b',
            Short::class to 's',
            Long::class to 'L',
            Float::class to 'f',
            Double::class to 'd'
        )

        fun serialize(value: MojangsonStructure): Component {
            return NbtSerializer(value, 4).serialize()
        }
    }
}
