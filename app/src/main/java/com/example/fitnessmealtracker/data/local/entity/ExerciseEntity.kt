package com.example.fitnessmealtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val workoutId: Long, // Външен ключ към WorkoutEntity
    val name: String,
    val sets: Int,
    val reps: Int,
    val weight: Double
)