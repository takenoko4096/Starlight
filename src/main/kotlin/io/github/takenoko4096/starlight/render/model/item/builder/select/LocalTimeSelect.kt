package io.github.takenoko4096.starlight.render.model.item.builder.select

import com.ibm.icu.util.TimeZone
import com.ibm.icu.util.ULocale

class LocalTimeSelect(val format: String, val locale: ULocale, val timeZone: TimeZone) : Select<String>() {
}
