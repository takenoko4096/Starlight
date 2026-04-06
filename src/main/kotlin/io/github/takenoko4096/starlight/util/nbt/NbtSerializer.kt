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
import kotlin.toString

class NbtSerializer private constructor(
    private val value: MojangsonStructure,
    private val indentationSpaceCount: Int
) {
    companion object {
        private const val LINE_BREAK: Char = '\n'
        private const val QUOTE: Char = '"'
        private const val COLON: Char = ':'
        private const val COMMA: Char = ','
        private const val SEMICOLON: Char = ';'
        private val COMPOUND_BRACES: CharArray = charArrayOf('{', '}')
        private val ARRAY_LIST_BRACES: CharArray = charArrayOf('[', ']')
        private const val WHITESPACE: Char = ' '
        private const val ESCAPE: Char = '\\'
        private val SYMBOLS_ON_STRING = mutableSetOf<Char>()
        private val ITERABLE_TYPE_SYMBOLS: Map<KClass<out MojangsonIterable<*>>, Char> = mapOf(
            MojangsonByteArray::class to 'B',
            MojangsonIntArray::class to 'I',
            MojangsonLongArray::class to 'L'
        )
        private val NUMBER_TYPE_SYMBOLS: Map<KClass<out Number>, Char> = mapOf(
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

    private fun serialize(): Component {
        return serialize(value, 1)
    }

    private fun serialize(value: Any?, indentation: Int): Component {
        return when (value) {
            null -> component { text("null") }
            is Boolean -> bool(value)
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
            text(COMPOUND_BRACES[0].toString())

            section {
                for (i in keys.indices) {
                    val key = keys[i]

                    try {
                        val childValue: Any = compound.get(key, compound.getTypeOf(key))

                        text(
                            LINE_BREAK + indentation(indentation + 1) + string(key) + COLON + WHITESPACE + serialize(childValue, indentation + 1)
                        )
                    }
                    catch (e: IllegalArgumentException) {
                        throw NbtSerializationException(
                            "キー '$key' における無効な型: ${compound.getTypeOf(key)}",
                            e
                        )
                    }

                    if (i != keys.size - 1) {
                        text(COMMA.toString())
                    }
                }

                if (keys.isNotEmpty()) {
                    text(LINE_BREAK + indentation(indentation))
                }
            }

            text(COMPOUND_BRACES[1].toString())
        }
    }

    private fun iterable(iterable: MojangsonIterable<*>, indentation: Int): Component {
        return component {
            textColor(VanillaColor.WHITE)
            text(ARRAY_LIST_BRACES[0].toString())

            section {
                if (ITERABLE_TYPE_SYMBOLS.containsKey(iterable::class)) {
                    text(ITERABLE_TYPE_SYMBOLS[iterable::class].toString())
                    text(SEMICOLON.toString())
                }

                for ((i, element) in iterable.withIndex()) {
                    if (i >= 1) {
                        text(COMMA.toString())
                    }

                    try {
                        text(LINE_BREAK.toString())
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
                    text(LINE_BREAK.toString())
                    text(indentation(indentation))
                }
            }

            text(ARRAY_LIST_BRACES[1].toString())
        }
    }

    private fun string(value: String): Component {
        val requireQuote = SYMBOLS_ON_STRING.stream()
            .anyMatch { sym: Char -> value.contains(sym.toString()) }

        return component {
            if (requireQuote) {
                text(QUOTE.toString())
            }

            text(value.replace(QUOTE.toString(), ESCAPE.toString().repeat(2) + QUOTE))

            if (requireQuote) {
                text(QUOTE.toString())
            }
        }
    }

    private fun bool(value: Boolean): Component {
        return component {
            if (value) text("true") else text("false")
        }
    }

    private fun number(value: Number): Component {
        return component {
            text(value.toString())

            if (NUMBER_TYPE_SYMBOLS.contains(value::class)) {
                text(NUMBER_TYPE_SYMBOLS[value::class].toString())
            }
        }
    }

    private fun indentation(indentation: Int): String {
        return WHITESPACE.toString().repeat(this.indentationSpaceCount).repeat(indentation - 1)
    }
}
