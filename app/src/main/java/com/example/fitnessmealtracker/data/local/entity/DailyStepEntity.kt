package com.example.fitnessmealtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_steps")
data class DailyStepEntity(
    @PrimaryKey
    val date: String,
    val steps: Int
)