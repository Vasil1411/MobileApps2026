package com.example.fitnessmealtracker.di

import android.content.Context
import com.example.fitnessmealtracker.data.local.AppDatabase
import com.example.fitnessmealtracker.data.remote.OpenFoodFactsApi
import com.example.fitnessmealtracker.data.repository.MealRepositoryImpl
import com.example.fitnessmealtracker.data.repository.WorkoutRepositoryImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppModule(private val context: Context) {


    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }


    val openFoodFactsApi: OpenFoodFactsApi by lazy {
        Retrofit.Builder()
            .baseUrl(OpenFoodFactsApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodFactsApi::class.java)
    }


    val mealRepository: MealRepositoryImpl by lazy {
        MealRepositoryImpl(database.mealDao())
    }

    val workoutRepository: WorkoutRepositoryImpl by lazy {
        WorkoutRepositoryImpl(database.workoutDao())
    }
}