package com.example.fitnessmealtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.fitnessmealtracker.data.local.entity.DailyStepEntity


@Dao
interface StepDao {
    @Query("SELECT steps FROM daily_steps WHERE date = :date")
    fun getStepsForDate(date: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(dailyStep: DailyStepEntity)

    @Query("SELECT * FROM daily_steps ORDER BY date DESC")
    fun getAllStepHistory(): Flow<List<DailyStepEntity>>
}