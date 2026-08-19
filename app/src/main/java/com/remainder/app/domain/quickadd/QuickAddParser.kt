package com.remainder.app.domain.quickadd

import java.time.DateTimeException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.temporal.TemporalAdjusters
import java.util.Locale

data class QuickAddResult(
    val title: String,
    val date: LocalDate?,
    val time: LocalTime?,
)

/**
 * Rule-based, fully on-device parser for quick-add free text (e.g. "Pay rent on the 1st at 9am").
 * Deliberately conservative: ambiguous phrases (like a bare "at 9" with no am/pm) are left
 * unparsed rather than guessed, since a silent misparse would create a wrong reminder.
 */
object QuickAddParser {

    private val timeRegex = Regex(
        """\b(?:at\s+)?(\d{1,2})(?::(\d{2}))?\s*(am|pm)\b""",
        RegexOption.IGNORE_CASE,
    )
    private val time24Regex = Regex(
        """\b(?:at\s+)?([01]?\d|2[0-3]):([0-5]\d)\b""",
    )
    private val todayRegex = Regex("""\b(?:on\s+)?today\b""", RegexOption.IGNORE_CASE)
    private val tomorrowRegex = Regex("""\b(?:on\s+)?tomorrow\b""", RegexOption.IGNORE_CASE)
    private val weekdayRegex = Regex(
        """\b(?:on\s+)?(next\s+)?(monday|tuesday|wednesday|thursday|friday|saturday|sunday)\b""",
        RegexOption.IGNORE_CASE,
    )
    private val ordinalDayRegex = Regex(
        """\b(?:on\s+)?the\s+(\d{1,2})(st|nd|rd|th)\b""",
        RegexOption.IGNORE_CASE,
    )
    private val monthNames =
        "january|february|march|april|may|june|july|august|september|october|november|december|" +
            "jan|feb|mar|apr|jun|jul|aug|sep|sept|oct|nov|dec"
    private val monthDayRegex = Regex(
        """\b(?:on\s+)?($monthNames)\.?\s+(\d{1,2})(?:st|nd|rd|th)?\b""",
        RegexOption.IGNORE_CASE,
    )
    private val dayMonthRegex = Regex(
        """\b(?:on\s+)?(\d{1,2})(?:st|nd|rd|th)?\s+($monthNames)\.?\b""",
        RegexOption.IGNORE_CASE,
    )

    fun parse(input: String, referenceDate: LocalDate = LocalDate.now()): QuickAddResult {
        var working = input
        var date: LocalDate? = null
        var time: LocalTime? = null

        timeRegex.find(working)?.let { match ->
            val hour = match.groupValues[1].toInt()
            val minute = match.groupValues[2].toIntOrNull() ?: 0
            val meridiem = match.groupValues[3].lowercase(Locale.ROOT)
            if (hour in 1..12 && minute in 0..59) {
                val hour24 = when {
                    meridiem == "am" && hour == 12 -> 0
                    meridiem == "pm" && hour != 12 -> hour + 12
                    else -> hour
                }
                time = LocalTime.of(hour24, minute)
                working = working.removeRange(match.range)
            }
        }
        if (time == null) {
            time24Regex.find(working)?.let { match ->
                time = LocalTime.of(match.groupValues[1].toInt(), match.groupValues[2].toInt())
                working = working.removeRange(match.range)
            }
        }

        monthDayRegex.find(working)?.let { match ->
            resolveMonthDay(match.groupValues[1], match.groupValues[2].toInt(), referenceDate)?.let {
                date = it
                working = working.removeRange(match.range)
            }
        }
        if (date == null) {
            dayMonthRegex.find(working)?.let { match ->
                resolveMonthDay(match.groupValues[2], match.groupValues[1].toInt(), referenceDate)?.let {
                    date = it
                    working = working.removeRange(match.range)
                }
            }
        }
        if (date == null) {
            ordinalDayRegex.find(working)?.let { match ->
                resolveDayOfMonth(match.groupValues[1].toInt(), referenceDate)?.let {
                    date = it
                    working = working.removeRange(match.range)
                }
            }
        }
        if (date == null) {
            weekdayRegex.find(working)?.let { match ->
                val next = match.groupValues[1].isNotEmpty()
                date = resolveWeekday(match.groupValues[2], next, referenceDate)
                working = working.removeRange(match.range)
            }
        }
        if (date == null) {
            tomorrowRegex.find(working)?.let { match ->
                date = referenceDate.plusDays(1)
                working = working.removeRange(match.range)
            }
        }
        if (date == null) {
            todayRegex.find(working)?.let { match ->
                date = referenceDate
                working = working.removeRange(match.range)
            }
        }

        return QuickAddResult(title = cleanTitle(working), date = date, time = time)
    }

    private fun resolveMonthDay(monthText: String, day: Int, referenceDate: LocalDate): LocalDate? {
        val month = parseMonth(monthText) ?: return null
        return try {
            val candidate = LocalDate.of(referenceDate.year, month, day)
            if (candidate.isBefore(referenceDate)) candidate.plusYears(1) else candidate
        } catch (e: DateTimeException) {
            null
        }
    }

    private fun resolveDayOfMonth(day: Int, referenceDate: LocalDate): LocalDate? {
        return try {
            val candidate = LocalDate.of(referenceDate.year, referenceDate.month, day)
            if (candidate.isBefore(referenceDate)) {
                val nextMonth = referenceDate.plusMonths(1)
                LocalDate.of(nextMonth.year, nextMonth.month, day)
            } else {
                candidate
            }
        } catch (e: DateTimeException) {
            null
        }
    }

    private fun resolveWeekday(name: String, forceNext: Boolean, referenceDate: LocalDate): LocalDate {
        val dayOfWeek = DayOfWeek.valueOf(name.uppercase(Locale.ROOT))
        val nextOrSame = referenceDate.with(TemporalAdjusters.nextOrSame(dayOfWeek))
        return if (forceNext && nextOrSame == referenceDate) {
            nextOrSame.plusWeeks(1)
        } else {
            nextOrSame
        }
    }

    private fun parseMonth(text: String): Month? {
        val normalized = text.lowercase(Locale.ROOT).removeSuffix(".")
        return when (normalized) {
            "jan", "january" -> Month.JANUARY
            "feb", "february" -> Month.FEBRUARY
            "mar", "march" -> Month.MARCH
            "apr", "april" -> Month.APRIL
            "may" -> Month.MAY
            "jun", "june" -> Month.JUNE
            "jul", "july" -> Month.JULY
            "aug", "august" -> Month.AUGUST
            "sep", "sept", "september" -> Month.SEPTEMBER
            "oct", "october" -> Month.OCTOBER
            "nov", "november" -> Month.NOVEMBER
            "dec", "december" -> Month.DECEMBER
            else -> null
        }
    }

    private fun cleanTitle(text: String): String {
        return text
            .replace(Regex("""\s+"""), " ")
            .trim()
            .trim(',', '.', '-', '–', '—')
            .trim()
    }
}
