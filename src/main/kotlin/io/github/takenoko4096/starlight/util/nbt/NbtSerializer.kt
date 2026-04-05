package io.github.takenoko4096.starlight.util.nbt

import com.gmail.takenokoii78.mojangson.MojangsonSerializationException
import com.gmail.takenokoii78.mojangson.MojangsonValue
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound
import com.gmail.takenokoii78.mojangson.values.MojangsonIterable
import com.gmail.takenokoii78.mojangson.values.MojangsonNull
import com.gmail.takenokoii78.mojangson.values.MojangsonNumber
import com.gmail.takenokoii78.mojangson.values.MojangsonPrimitive
import com.gmail.takenokoii78.mojangson.values.MojangsonString
import com.gmail.takenokoii78.mojangson.values.MojangsonStructure
import io.github.takenoko4096.starlight.util.text.VanillaColor
import io.github.takenoko4096.starlight.util.text.component
import net.minecraft.network.chat.Component
import java.util.function.IntFunction
import java.util.function.Predicate
import kotlin.arrayOfNulls
import kotlin.charArrayOf
import kotlin.compareTo
import kotlin.text.StringBuilder
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
        private val SIGNS: MutableSet<Char> = mutableSetOf('+', '-')
        private const val DECIMAL_POINT: Char = '.'
        private val SYMBOLS_ON_STRING = mutableSetOf<Char>()
        private val ITERABLE_TYPE_SYMBOLS: MutableMap<Class<out MojangsonIterable<*>>, Char> = null
        private val NUMBER_TYPE_SYMBOLS: MutableMap<Class<out Number>, Char>? = null
    }

    private fun serialize(): StringBuilder {
        return this.serialize(this.value, 1)
    }

    private fun serialize(value: Any?, indentation: Int): Component {
        return when (value) {
            null -> StringBuilder("null")
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

    private fun iterable(iterable: MojangsonIterable<*>, indentation: Int): StringBuilder {
        val stringBuilder = (StringBuilder()).append(ARRAY_LIST_BRACES[0])
        if (ITERABLE_TYPE_SYMBOLS.containsKey(iterable.getClass())) {
            stringBuilder.append(ITERABLE_TYPE_SYMBOLS.get(iterable.getClass())).append(';')
        }

        var i = 0

        for (element in iterable) {
            if (i >= 1) {
                stringBuilder.append(',')
            }

            try {
                stringBuilder.append('\n').append(this.indentation(indentation + 1))
                    .append(this.serialize(element, indentation + 1))
            } catch (e: IllegalArgumentException) {
                throw NbtSerializationException(
                    "インデックス'" + i + "における無効な型: " + element.getClass().getName(), e
                )
            }

            ++i
        }

        if (!iterable.isEmpty()) {
            stringBuilder.append('\n').append(this.indentation(indentation))
        }

        return stringBuilder.append(ARRAY_LIST_BRACES[1])
    }

    private fun string(value: kotlin.String): StringBuilder {
        val requireQuote = this.asJsonString || SYMBOLS_ON_STRING.stream()
            .anyMatch(Predicate { sym: Char? -> value.contains(sym.toString()) })
        val stringBuilder = StringBuilder()
        if (requireQuote) {
            stringBuilder.append('"')
        }

        val var10002 = String.valueOf('"')
        val var10003 = String.valueOf('\\')
        stringBuilder.append(value.replaceAll(var10002, var10003.repeat(2) + "\""))
        if (requireQuote) {
            stringBuilder.append('"')
        }

        return stringBuilder
    }

    private fun bool(value: Boolean): StringBuilder {
        return if (value) StringBuilder("true") else StringBuilder("false")
    }

    private fun number(value: Number): StringBuilder {
        val stringBuilder = StringBuilder(String.valueOf(value))
        if (!this.asJsonString && NUMBER_TYPE_SYMBOLS!!.containsKey(value.getClass())) {
            stringBuilder.append(NUMBER_TYPE_SYMBOLS.get(value.getClass()))
        }

        return stringBuilder
    }

    private fun indentation(indentation: Int): kotlin.String {
        return String.valueOf(' ').repeat(this.indentationSpaceCount).repeat(indentation - 1)
    }
}
