package com.example.fitnessmealtracker.data.repository

import com.example.fitnessmealtracker.data.local.dao.WorkoutDao
import com.example.fitnessmealtracker.data.local.entity.ExerciseEntity
import com.example.fitnessmealtracker.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

class WorkoutRepositoryImpl(private val workoutDao: WorkoutDao) {


    val allWorkouts: Flow<List<WorkoutEntity>> = workoutDao.getAllWorkouts()

    suspend fun insertWorkout(workout: WorkoutEntity): Long {
        return workoutDao.insertWorkout(workout)
    }

    suspend fun updateWorkout(workout: WorkoutEntity) {
        workoutDao.updateWorkout(workout)
    }

    suspend fun deleteWorkout(workout: WorkoutEntity) {
        workoutDao.deleteWorkout(workout)
    }


    fun getExercisesForWorkout(workoutId: Long): Flow<List<ExerciseEntity>> {
        return workoutDao.getExercisesForWorkout(workoutId)
    }

    suspend fun insertExercise(exercise: ExerciseEntity) {
        workoutDao.insertExercise(exercise)
    }

    suspend fun updateExercise(exercise: ExerciseEntity) {
        workoutDao.updateExercise(exercise)
    }

    suspend fun deleteExercise(exercise: ExerciseEntity) {
        workoutDao.deleteExercise(exercise)
    }
}