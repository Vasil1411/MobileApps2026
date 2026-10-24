package com.example.fitnessmealtracker.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessmealtracker.data.local.AppDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val mealDao = database.mealDao()
    private val workoutDao = database.workoutDao()

    private val todayDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())

    val totalCaloriesConsumed: StateFlow<Int?> = mealDao.getTotalCaloriesForDate(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCaloriesBurned: StateFlow<Int?> = workoutDao.getTotalCaloriesBurnedForDate(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}