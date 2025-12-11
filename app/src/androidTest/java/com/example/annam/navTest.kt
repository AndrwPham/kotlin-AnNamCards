package com.example.annam

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class MyComposeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun navTest() {

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        composeTestRule.setContent {
            Navigator(navController, networkService)
        }

        assertEquals("menu", navController.currentDestination?.route)

    }

    @Test
    fun homeStart() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        composeTestRule.setContent {
            Navigator(navController, networkService)
        }
        composeTestRule.onNodeWithContentDescription("navToAddCard")
            .assertExists()
            .assertTextEquals("Add Card")
            .performClick()

        assertEquals("add_card", navController.currentDestination?.route)
    }
}