package com.example.fitnessmealtracker.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessmealtracker.data.local.AppDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val mealDao = database.mealDao()
    private val workoutDao = database.workoutDao()
    private val stepDao = database.stepDao() // <-- Взимаме StepDao

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    private val _selectedDate = MutableStateFlow(dateFormat.format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate

    fun updateDate(newDate: String) {
        _selectedDate.value = newDate
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalCaloriesConsumed: StateFlow<Int?> = _selectedDate.flatMapLatest { date ->
        mealDao.getTotalCaloriesForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalCaloriesBurned: StateFlow<Int?> = _selectedDate.flatMapLatest { date ->
        workoutDao.getTotalCaloriesBurnedForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Динамично зареждане на стъпките от базата спрямо избраната дата
    @OptIn(ExperimentalCoroutinesApi::class)
    val stepsForSelectedDate: StateFlow<Int?> = _selectedDate.flatMapLatest { date ->
        stepDao.getStepsForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}