package com.example.fitnessmealtracker.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitnessmealtracker.data.local.entity.ExerciseEntity
import com.example.fitnessmealtracker.data.local.entity.WorkoutEntity
import com.example.fitnessmealtracker.data.repository.WorkoutRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutsViewModel(private val repository: WorkoutRepositoryImpl) : ViewModel() {

    val allWorkouts: StateFlow<List<WorkoutEntity>> = repository.allWorkouts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertWorkout(title: String, date: String, duration: Int, calories: Int, onWorkoutInserted: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val workoutId = repository.insertWorkout(
                WorkoutEntity(
                    title = title,
                    date = date,
                    durationMinutes = duration,
                    caloriesBurned = calories
                )
            )
            onWorkoutInserted(workoutId)
        }
    }

    fun updateWorkout(workout: WorkoutEntity) {
        viewModelScope.launch {
            repository.updateWorkout(workout)
        }
    }

    fun deleteWorkout(workout: WorkoutEntity) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
        }
    }

    // --- ДОБАВИ ТУК ТОЗИ МЕТОД ЗА УПРАЖНЕНИЯТА ---
    fun getExercisesForWorkout(workoutId: Long): Flow<List<ExerciseEntity>> {
        return repository.getExercisesForWorkout(workoutId)
    }

    fun insertExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.insertExercise(exercise)
        }
    }

    fun updateExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.updateExercise(exercise)
        }
    }

    fun deleteExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.deleteExercise(exercise)
        }
    }
}

class WorkoutsViewModelFactory(private val repository: WorkoutRepositoryImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}