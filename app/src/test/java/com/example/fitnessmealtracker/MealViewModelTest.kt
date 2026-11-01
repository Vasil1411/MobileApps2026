package com.example.fitnessmealtracker

import com.example.fitnessmealtracker.data.repository.MealRepositoryImpl
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import com.example.fitnessmealtracker.ui.meals.MealViewModel


class MealViewModelTest {

    private lateinit var viewModel: MealViewModel
    private val repository: MealRepositoryImpl = mockk(relaxed = true)

    @Before
    fun setup() {
        viewModel = MealViewModel(repository)
    }

    @Test
    fun testAppendScannedProduct_accumulatesCorrectly() {
        // Добавяме 200g продукт с 100 kcal и 10g протеин на 100g
        viewModel.appendScannedProduct("Chicken", 100.0, 10.0, 5.0, 2.0, 200.0)

        assertEquals("Chicken (200g)", viewModel.mealNameInput)
        assertEquals("200", viewModel.caloriesInput) // 100 kcal * 2
        assertEquals("20.0", viewModel.proteinInput) // 10g * 2
    }

    @Test
    fun testUndoLastAction_revertsForm() {
        viewModel.appendScannedProduct("Chicken", 100.0, 10.0, 5.0, 2.0, 100.0)
        viewModel.appendScannedProduct("Rice", 200.0, 4.0, 40.0, 1.0, 100.0)

        // Правим Undo на втория продукт (Rice)
        viewModel.undoLastAction()

        assertEquals("Chicken (100g)", viewModel.mealNameInput)
        assertEquals("100", viewModel.caloriesInput)
    }
}