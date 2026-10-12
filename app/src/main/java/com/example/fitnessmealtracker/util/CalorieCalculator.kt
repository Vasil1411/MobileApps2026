package com.example.fitnessmealtracker.util

import com.example.fitnessmealtracker.data.local.entity.MealEntity
import com.example.fitnessmealtracker.data.local.entity.WorkoutEntity

object CalorieCalculator {

    // Изчислява общо приетите калории за определена дата
    fun calculateTotalConsumedCalories(meals: List<MealEntity>, targetDate: String): Int {
        return meals.filter { it.date == targetDate }.sumOf { it.calories }
    }

    // Изчислява общо изгорените калории от тренировки за определена дата
    fun calculateTotalBurnedCalories(workouts: List<WorkoutEntity>, targetDate: String): Int {
        return workouts.filter { it.date == targetDate }.sumOf { it.caloriesBurned }
    }

    // Калкулира днелния баланс (Приети минус Изгорени, или чисто калорийно съдържание спрямо цел)
    fun calculateDailyNetCalories(
        meals: List<MealEntity>,
        workouts: List<WorkoutEntity>,
        targetDate: String
    ): Int {
         val BASE_BMR = 2000 // Примерно базово метаболитно ниво (може да се направи динамично)
        val consumed = calculateTotalConsumedCalories(meals, targetDate)
        val burned = calculateTotalBurnedCalories(workouts, targetDate)

        // Нетен калориен баланс = Базов метаболизъм + Приета храна - Изгорени от тренировка
        return BASE_BMR + consumed - burned
    }
}