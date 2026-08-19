package com.remainder.app.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.remainder.app.domain.model.Action
import com.remainder.app.domain.model.Priority
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.RepeatType
import com.remainder.app.ui.theme.RemainderTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class ActionCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun sampleAction(isCompleted: Boolean = false) = Action(
        title = "Call client",
        scheduledDate = LocalDate.now(),
        scheduledTime = LocalTime.of(10, 30),
        reminderOffset = ReminderOffset.AT_TIME,
        repeatType = RepeatType.NEVER,
        categoryId = null,
        priority = Priority.MEDIUM,
        isCompleted = isCompleted,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH,
    )

    @Test
    fun displaysActionTitle() {
        composeTestRule.setContent {
            RemainderTheme {
                ActionCard(action = sampleAction(), categoryName = "Work", onClick = {}, onToggleComplete = {})
            }
        }

        composeTestRule.onNodeWithText("Call client").assertExists()
    }

    @Test
    fun clickingCard_invokesOnClick() {
        var clicked = false
        composeTestRule.setContent {
            RemainderTheme {
                ActionCard(
                    action = sampleAction(),
                    categoryName = null,
                    onClick = { clicked = true },
                    onToggleComplete = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Call client").performClick()

        assertTrue(clicked)
    }

    @Test
    fun togglingCheckbox_invokesOnToggleComplete() {
        var toggled = false
        composeTestRule.setContent {
            RemainderTheme {
                ActionCard(
                    action = sampleAction(),
                    categoryName = null,
                    onClick = {},
                    onToggleComplete = { toggled = true },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Mark Call client as complete").performClick()

        assertTrue(toggled)
    }
}
