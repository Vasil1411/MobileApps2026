package com.example.fitnessmealtracker.util

import com.example.fitnessmealtracker.data.local.entity.MealEntity
import com.example.fitnessmealtracker.data.local.entity.WorkoutEntity

object CalorieCalculator {

    // Сумира приетите калории САМО за избраната дата
    fun calculateTotalConsumedCalories(meals: List<MealEntity>, selectedDate: String): Int {
        return meals
            .filter { it.date.trim() == selectedDate.trim() }
            .sumOf { it.calories }
    }

    // Сумира изгорените калории САМО за избраната дата
    fun calculateTotalBurnedCalories(workouts: List<WorkoutEntity>, selectedDate: String): Int {
        return workouts
            .filter { it.date.trim() == selectedDate.trim() }
            .sumOf { it.caloriesBurned }
    }

    fun calculateDailyNetCalories(
        meals: List<MealEntity>,
        workouts: List<WorkoutEntity>,
        targetDate: String
    ): Int {
        val consumed = calculateTotalConsumedCalories(meals, targetDate)
        val burned = calculateTotalBurnedCalories(workouts, targetDate)
        return consumed - burned
    }
}