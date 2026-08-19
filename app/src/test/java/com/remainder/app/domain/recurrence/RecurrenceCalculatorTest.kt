package com.remainder.app.domain.recurrence

import com.remainder.app.domain.model.RepeatType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class RecurrenceCalculatorTest {

    @Test
    fun never_returnsSameDate() {
        val date = LocalDate.of(2026, 3, 10)
        assertEquals(date, RecurrenceCalculator.nextOccurrence(date, RepeatType.NEVER))
    }

    @Test
    fun daily_addsOneDay() {
        val date = LocalDate.of(2026, 3, 10)
        assertEquals(LocalDate.of(2026, 3, 11), RecurrenceCalculator.nextOccurrence(date, RepeatType.DAILY))
    }

    @Test
    fun weekly_addsSevenDays() {
        val date = LocalDate.of(2026, 3, 10)
        assertEquals(LocalDate.of(2026, 3, 17), RecurrenceCalculator.nextOccurrence(date, RepeatType.WEEKLY))
    }

    @Test
    fun monthly_addsOneMonth() {
        val date = LocalDate.of(2026, 3, 10)
        assertEquals(LocalDate.of(2026, 4, 10), RecurrenceCalculator.nextOccurrence(date, RepeatType.MONTHLY))
    }

    @Test
    fun monthly_fromJan31_clampsToFeb28() {
        val date = LocalDate.of(2026, 1, 31)
        assertEquals(LocalDate.of(2026, 2, 28), RecurrenceCalculator.nextOccurrence(date, RepeatType.MONTHLY))
    }

    @Test
    fun yearly_addsOneYear() {
        val date = LocalDate.of(2026, 3, 10)
        assertEquals(LocalDate.of(2027, 3, 10), RecurrenceCalculator.nextOccurrence(date, RepeatType.YEARLY))
    }

    @Test
    fun yearly_fromLeapDay_clampsToFeb28() {
        val date = LocalDate.of(2028, 2, 29)
        assertEquals(LocalDate.of(2029, 2, 28), RecurrenceCalculator.nextOccurrence(date, RepeatType.YEARLY))
    }
}
