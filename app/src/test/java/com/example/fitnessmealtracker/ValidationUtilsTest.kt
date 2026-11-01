package com.example.fitnessmealtracker

import org.junit.Assert.assertTrue
import org.junit.Test
import com.example.fitnessmealtracker.util.ValidationResult
import com.example.fitnessmealtracker.util.ValidationUtils

class ValidationUtilsTest {

    @Test
    fun testValidateMeal_validInput_returnsSuccess() {
        val result = ValidationUtils.validateMeal(
            name = "Здравословен обяд",
            calories = 450,
            protein = 35.0,
            date = "18.10.2026"
        )
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun testValidateMeal_invalidCalories_returnsError() {
        val result = ValidationUtils.validateMeal(
            name = "Обяд",
            calories = -5,
            protein = 20.0,
            date = "18.10.2026"
        )
        assertTrue(result is ValidationResult.Error)
    }
}