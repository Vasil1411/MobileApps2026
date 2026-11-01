package com.example.fitnessmealtracker.ui.workouts

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessmealtracker.data.local.AppDatabase
import com.example.fitnessmealtracker.data.local.entity.DailyStepEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedometerViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val database = AppDatabase.getDatabase(application)
    private val stepDao = database.stepDao()

    private val sharedPreferences = application.getSharedPreferences("pedometer_prefs", Context.MODE_PRIVATE)

    var stepCount by mutableStateOf(0)
        private set

    var isSensorAvailable by mutableStateOf(stepSensor != null)
        private set

    val allStepHistory: Flow<List<DailyStepEntity>> = stepDao.getAllStepHistory()

    init {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0]
            // Уеднаквяваме формата на датата с целия проект: dd.MM.yyyy
            val currentDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())

            val savedDate = sharedPreferences.getString("saved_date", "") ?: ""
            var baselineSteps = sharedPreferences.getFloat("baseline_steps", -1f)

            if (savedDate.isNotEmpty() && savedDate != currentDate) {
                val lastSteps = sharedPreferences.getInt("last_step_count", 0)
                viewModelScope.launch {
                    stepDao.insertOrUpdate(DailyStepEntity(date = savedDate, steps = lastSteps))
                }
            }

            if (savedDate != currentDate || baselineSteps < 0f || totalSteps < baselineSteps) {
                baselineSteps = totalSteps
                sharedPreferences.edit()
                    .putString("saved_date", currentDate)
                    .putFloat("baseline_steps", baselineSteps)
                    .apply()
            }

            val calculatedSteps = (totalSteps - baselineSteps).toInt()
            stepCount = if (calculatedSteps >= 0) calculatedSteps else 0

            sharedPreferences.edit().putInt("last_step_count", stepCount).apply()

            viewModelScope.launch {
                stepDao.insertOrUpdate(DailyStepEntity(date = currentDate, steps = stepCount))
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}