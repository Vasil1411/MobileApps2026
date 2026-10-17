package com.example.fitnessmealtracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fitnessmealtracker.data.local.dao.MealDao
import com.example.fitnessmealtracker.data.local.dao.WorkoutDao
import com.example.fitnessmealtracker.data.local.dao.StepDao
import com.example.fitnessmealtracker.data.local.entity.ExerciseEntity
import com.example.fitnessmealtracker.data.local.entity.MealEntity
import com.example.fitnessmealtracker.data.local.entity.WorkoutEntity
import com.example.fitnessmealtracker.data.local.entity.DailyStepEntity

@Database(
    entities = [WorkoutEntity::class, ExerciseEntity::class, MealEntity::class, DailyStepEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun mealDao(): MealDao
    abstract fun stepDao(): StepDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitness_meal_database"
                )
                    .fallbackToDestructiveMigration() // 2. Добави този ред, за да обновява базата автоматично при промени
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}