package com.example.fitnessmealtracker.data.repository

import com.example.fitnessmealtracker.data.local.dao.MealDao
import com.example.fitnessmealtracker.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow

class MealRepositoryImpl(private val mealDao: MealDao) {


    val allMeals: Flow<List<MealEntity>> = mealDao.getAllMeals()

    suspend fun insertMeal(meal: MealEntity) {
        mealDao.insertMeal(meal)
    }

    suspend fun updateMeal(meal: MealEntity) {
        mealDao.updateMeal(meal)
    }

    suspend fun deleteMeal(meal: MealEntity) {
        mealDao.deleteMeal(meal)
    }
}