package com.remainder.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeScreen_isStartDestination() {
        composeTestRule.onNodeWithText("Today's Actions").assertExists()
    }

    @Test
    fun addActionFab_isDisplayed() {
        composeTestRule.onNodeWithContentDescription("Add action").assertExists()
    }
}
