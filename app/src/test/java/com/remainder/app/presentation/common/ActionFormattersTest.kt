package com.remainder.app.presentation.common

import com.remainder.app.domain.model.Priority
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.RepeatType
import com.remainder.app.domain.model.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ActionFormattersTest {

    @Test
    fun priority_displayLabels() {
        assertEquals("Low", Priority.LOW.displayLabel())
        assertEquals("Medium", Priority.MEDIUM.displayLabel())
        assertEquals("High", Priority.HIGH.displayLabel())
    }

    @Test
    fun repeatType_displayLabels() {
        assertEquals("Never", RepeatType.NEVER.displayLabel())
        assertEquals("Daily", RepeatType.DAILY.displayLabel())
        assertEquals("Weekly", RepeatType.WEEKLY.displayLabel())
        assertEquals("Monthly", RepeatType.MONTHLY.displayLabel())
        assertEquals("Yearly", RepeatType.YEARLY.displayLabel())
    }

    @Test
    fun reminderOffset_displayLabels() {
        assertEquals("At time of action", ReminderOffset.AT_TIME.displayLabel())
        assertEquals("5 minutes before", ReminderOffset.MIN_5.displayLabel())
        assertEquals("1 hour before", ReminderOffset.HOUR_1.displayLabel())
        assertEquals("1 day before", ReminderOffset.DAY_1.displayLabel())
    }

    @Test
    fun themeMode_displayLabels() {
        assertEquals("Light", ThemeMode.LIGHT.displayLabel())
        assertEquals("Dark", ThemeMode.DARK.displayLabel())
        assertEquals("System default", ThemeMode.SYSTEM.displayLabel())
    }

    @Test
    fun localDate_today_displaysAsToday() {
        assertEquals("Today", LocalDate.now().displayLabel())
    }

    @Test
    fun localDate_tomorrow_displaysAsTomorrow() {
        assertEquals("Tomorrow", LocalDate.now().plusDays(1).displayLabel())
    }

    @Test
    fun localDate_otherDate_usesFormattedPattern() {
        val farFuture = LocalDate.now().plusDays(30)
        val label = farFuture.displayLabel()
        assertEquals(false, label == "Today")
        assertEquals(false, label == "Tomorrow")
    }
}
