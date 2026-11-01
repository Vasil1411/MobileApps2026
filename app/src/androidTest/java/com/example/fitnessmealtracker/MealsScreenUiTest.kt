package com.example.fitnessmealtracker

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.fitnessmealtracker.data.repository.MealRepositoryImpl
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import com.example.fitnessmealtracker.ui.meals.MealViewModel
import com.example.fitnessmealtracker.ui.meals.MealsScreen


class MealsScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository: MealRepositoryImpl = mockk(relaxed = true)

    @Test
    fun testMealsScreen_displaysEssentialButtonsAndFields() {
        val viewModel = MealViewModel(repository)

        composeTestRule.setContent {
            MealsScreen(viewModel = viewModel)
        }

        // Проверяваме дали бутонът за сканиране е видим
        composeTestRule.onNodeWithText("📷 Сканирай").assertIsDisplayed()

        // Проверяваме дали полето за име на храненето е видимо
        composeTestRule.onNodeWithText("Име на храненето").assertIsDisplayed()
    }

    @Test
    fun testMealsScreen_inputTypingUpdatesState() {
        val viewModel = MealViewModel(repository)

        composeTestRule.setContent {
            MealsScreen(viewModel = viewModel)
        }

        // Въвеждаме текст в полето за име на храненето
        composeTestRule.onNodeWithText("Име на храненето")
            .performTextInput("Овесени ядки")

        // Проверяваме дали ViewModel състоянието е отразило въведения текст
        assert(viewModel.mealNameInput == "Овесени ядки")
    }
}