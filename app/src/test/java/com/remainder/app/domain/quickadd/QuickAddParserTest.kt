package com.remainder.app.domain.quickadd

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class QuickAddParserTest {

    private val reference: LocalDate = LocalDate.of(2026, 8, 19) // a Wednesday

    @Test
    fun plainTitle_withNoDateOrTime_isLeftUntouched() {
        val result = QuickAddParser.parse("Buy groceries", reference)

        assertEquals("Buy groceries", result.title)
        assertNull(result.date)
        assertNull(result.time)
    }

    @Test
    fun today_isRecognized() {
        val result = QuickAddParser.parse("Call mom today", reference)

        assertEquals("Call mom", result.title)
        assertEquals(reference, result.date)
    }

    @Test
    fun tomorrow_isRecognized() {
        val result = QuickAddParser.parse("Call mom tomorrow", reference)

        assertEquals("Call mom", result.title)
        assertEquals(reference.plusDays(1), result.date)
    }

    @Test
    fun ordinalDayOfMonth_resolvesWithinCurrentMonth() {
        val result = QuickAddParser.parse("Pay rent on the 25th", reference)

        assertEquals("Pay rent", result.title)
        assertEquals(LocalDate.of(2026, 8, 25), result.date)
    }

    @Test
    fun ordinalDayOfMonth_alreadyPassed_rollsToNextMonth() {
        val result = QuickAddParser.parse("Pay rent on the 1st", reference)

        assertEquals("Pay rent", result.title)
        assertEquals(LocalDate.of(2026, 9, 1), result.date)
    }

    @Test
    fun weekdayName_resolvesToNextOccurrence() {
        val result = QuickAddParser.parse("Team sync on Friday", reference)

        assertEquals("Team sync", result.title)
        assertEquals(LocalDate.of(2026, 8, 21), result.date)
    }

    @Test
    fun weekdayName_withNextPrefix_skipsThisWeeksOccurrence() {
        val result = QuickAddParser.parse("Team sync next Wednesday", reference)

        assertEquals("Team sync", result.title)
        assertEquals(LocalDate.of(2026, 8, 26), result.date)
    }

    @Test
    fun monthAndDay_isRecognized() {
        val result = QuickAddParser.parse("Flight to NYC on Aug 25", reference)

        assertEquals("Flight to NYC", result.title)
        assertEquals(LocalDate.of(2026, 8, 25), result.date)
    }

    @Test
    fun dayAndMonth_isRecognized() {
        val result = QuickAddParser.parse("25 Aug submit report", reference)

        assertEquals("submit report", result.title)
        assertEquals(LocalDate.of(2026, 8, 25), result.date)
    }

    @Test
    fun monthAndDay_alreadyPassedThisYear_rollsToNextYear() {
        val result = QuickAddParser.parse("Anniversary on Jan 1", reference)

        assertEquals(LocalDate.of(2027, 1, 1), result.date)
    }

    @Test
    fun amTime_isRecognized() {
        val result = QuickAddParser.parse("Pay rent on the 1st at 9am", reference)

        assertEquals("Pay rent", result.title)
        assertEquals(LocalDate.of(2026, 9, 1), result.date)
        assertEquals(LocalTime.of(9, 0), result.time)
    }

    @Test
    fun pmTimeWithMinutes_isRecognized() {
        val result = QuickAddParser.parse("Doctor appointment tomorrow at 2:30pm", reference)

        assertEquals("Doctor appointment", result.title)
        assertEquals(reference.plusDays(1), result.date)
        assertEquals(LocalTime.of(14, 30), result.time)
    }

    @Test
    fun noon12pm_resolvesToHour12() {
        val result = QuickAddParser.parse("Lunch at 12pm", reference)

        assertEquals(LocalTime.of(12, 0), result.time)
    }

    @Test
    fun midnight12am_resolvesToHour0() {
        val result = QuickAddParser.parse("Backup job at 12am", reference)

        assertEquals(LocalTime.of(0, 0), result.time)
    }

    @Test
    fun twentyFourHourTime_isRecognized() {
        val result = QuickAddParser.parse("Meeting at 14:30", reference)

        assertEquals("Meeting", result.title)
        assertEquals(LocalTime.of(14, 30), result.time)
    }

    @Test
    fun ambiguousBareHour_isLeftUnparsed() {
        val result = QuickAddParser.parse("Call client at 9", reference)

        assertNull(result.time)
        assertEquals("Call client at 9", result.title)
    }

    @Test
    fun invalidCalendarDate_isLeftUnparsed() {
        val result = QuickAddParser.parse("Reminder on Feb 30", reference)

        assertNull(result.date)
        assertEquals("Reminder on Feb 30", result.title)
    }

    @Test
    fun titleWithLegitimateConnectorWords_isNotMangled() {
        val result = QuickAddParser.parse("Focus on the report", reference)

        assertEquals("Focus on the report", result.title)
        assertNull(result.date)
    }
}
