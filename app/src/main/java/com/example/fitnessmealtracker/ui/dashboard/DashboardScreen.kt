package com.example.fitnessmealtracker.ui.dashboard

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fitnessmealtracker.data.local.entity.MealEntity
import com.example.fitnessmealtracker.data.local.entity.WorkoutEntity
import com.example.fitnessmealtracker.ui.workouts.PedometerViewModel
import com.example.fitnessmealtracker.util.CalorieCalculator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel,
    pedometerViewModel: PedometerViewModel,
    meals: List<MealEntity>,
    workouts: List<WorkoutEntity>
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val todayStr = dateFormat.format(Date())

    var selectedDate by remember { mutableStateOf(todayStr) }
    var showDashboardDatePicker by remember { mutableStateOf(false) }

    val steps = pedometerViewModel.stepCount

    val consumedCalories = CalorieCalculator.calculateTotalConsumedCalories(meals, selectedDate)
    val burnedCalories = CalorieCalculator.calculateTotalBurnedCalories(workouts, selectedDate)
    val netBalance = consumedCalories - burnedCalories

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚡ Smart Fitness Dashboard", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Филтър за дата
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        val calendar = Calendar.getInstance()
                        try {
                            calendar.time = dateFormat.parse(selectedDate) ?: Date()
                        } catch (_: Exception) {}
                        calendar.add(Calendar.DAY_OF_YEAR, -1)
                        selectedDate = dateFormat.format(calendar.time)
                    }) {
                        Text("<")
                    }

                    TextButton(onClick = { showDashboardDatePicker = true }) {
                        Text(text = "📅 Дата: $selectedDate", style = MaterialTheme.typography.bodyLarge)
                    }

                    Button(onClick = {
                        val calendar = Calendar.getInstance()
                        try {
                            calendar.time = dateFormat.parse(selectedDate) ?: Date()
                        } catch (_: Exception) {}
                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                        selectedDate = dateFormat.format(calendar.time)
                    }) {
                        Text(">")
                    }
                }
            }

            if (showDashboardDatePicker) {
                val calendar = Calendar.getInstance()
                try {
                    calendar.time = dateFormat.parse(selectedDate) ?: Date()
                } catch (_: Exception) {}

                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        selectedDate = dateFormat.format(calendar.time)
                        showDashboardDatePicker = false
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    setOnCancelListener { showDashboardDatePicker = false }
                }.show()
            }

            // Активност / Стъпки
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("👟 Днешна активност", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$steps",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "/ 10,000 стъпки",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Калориен баланс спрямо избраната дата
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("🔥 Калориен баланс ($selectedDate)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Приети", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("$consumedCalories", style = MaterialTheme.typography.titleLarge, color = Color(0xFF4CAF50))
                            Text("kcal", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Изгорени", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("$burnedCalories", style = MaterialTheme.typography.titleLarge, color = Color(0xFF03A9F4))
                            Text("kcal", style = MaterialTheme.typography.bodySmall, color = Color(0xFF03A9F4))
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Баланс", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("$netBalance", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                            Text("kcal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        }
    }
}