package com.example.fitnessmealtracker.ui.workouts

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fitnessmealtracker.data.local.entity.WorkoutEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsScreen(viewModel: WorkoutsViewModel) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val workouts by viewModel.allWorkouts.collectAsState(initial = emptyList())

    var selectedWorkoutForExercises by remember { mutableStateOf<WorkoutEntity?>(null) }

    var showAddDialog by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf(dateFormat.format(Date())) }
    var showAddDatePicker by remember { mutableStateOf(false) }
    var durationInput by remember { mutableStateOf("") }
    var caloriesInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var workoutToEdit by remember { mutableStateOf<WorkoutEntity?>(null) }
    var editTitleInput by remember { mutableStateOf("") }
    var editDateInput by remember { mutableStateOf("") }
    var showEditDatePicker by remember { mutableStateOf(false) }
    var editDurationInput by remember { mutableStateOf("") }
    var editCaloriesInput by remember { mutableStateOf("") }

    if (selectedWorkoutForExercises != null) {
        ExerciseDetailScreen(
            workout = selectedWorkoutForExercises!!,
            viewModel = viewModel,
            onBack = { selectedWorkoutForExercises = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Smart Fitness - Тренировки") })
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    dateInput = dateFormat.format(Date())
                    showAddDialog = true
                }) {
                    Text("+", style = MaterialTheme.typography.headlineMedium)
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(text = "Моите тренировки:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                if (workouts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Все още няма добавени тренировки. Натисни '+' долу вдясно.")
                    }
                } else {
                    workouts.forEach { workout ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = workout.title, style = MaterialTheme.typography.bodyLarge)
                                    Row {
                                        TextButton(onClick = {
                                            workoutToEdit = workout
                                            editTitleInput = workout.title
                                            editDateInput = workout.date.ifBlank { dateFormat.format(Date()) }
                                            editDurationInput = workout.durationMinutes.toString()
                                            editCaloriesInput = workout.caloriesBurned.toString()
                                        }) {
                                            Text("Редактирай")
                                        }
                                        TextButton(onClick = { viewModel.deleteWorkout(workout) }) {
                                            Text("Изтрий", color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                                Text(text = "⏱ Продължителност: ${workout.durationMinutes} мин")
                                Text(text = "🔥 Изгорени калории: ${workout.caloriesBurned} kcal")
                                Text(text = "📅 Дата: ${workout.date}", style = MaterialTheme.typography.bodySmall)

                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { selectedWorkoutForExercises = workout },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("💪 Управление на упражненията")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Нова тренировка") },
            text = {
                Column {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Име (напр. Гърди / Гръб)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = durationInput,
                        onValueChange = { durationInput = it },
                        label = { Text("Продължителност (мин)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = caloriesInput,
                        onValueChange = { caloriesInput = it },
                        label = { Text("Изгорени калории") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAddDatePicker = true }
                    ) {
                        OutlinedTextField(
                            value = dateInput,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Дата (ДД.ММ.ГГГГ)") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    if (showAddDatePicker) {
                        val calendar = Calendar.getInstance()
                        try {
                            calendar.time = dateFormat.parse(dateInput) ?: Date()
                        } catch (_: Exception) {}

                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                calendar.set(year, month, dayOfMonth)
                                dateInput = dateFormat.format(calendar.time)
                                showAddDatePicker = false
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            setOnCancelListener { showAddDatePicker = false }
                        }.show()
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val duration = durationInput.toIntOrNull()
                    val calories = caloriesInput.toIntOrNull()

                    if (titleInput.isBlank() || duration == null || duration <= 0 || calories == null || calories < 0) {
                        errorMessage = "Моля, попълнете правилно всички полета (име и валидни числа)!"
                    } else {
                        errorMessage = null
                        viewModel.insertWorkout(
                            title = titleInput,
                            date = dateInput,
                            duration = duration,
                            calories = calories
                        )
                        titleInput = ""
                        durationInput = ""
                        caloriesInput = ""
                        showAddDialog = false
                    }
                }) {
                    Text("Запиши")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Отказ")
                }
            }
        )
    }

    workoutToEdit?.let { workout ->
        AlertDialog(
            onDismissRequest = { workoutToEdit = null },
            title = { Text("Редактиране на тренировка") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editTitleInput,
                        onValueChange = { editTitleInput = it },
                        label = { Text("Име") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editDurationInput,
                        onValueChange = { editDurationInput = it },
                        label = { Text("Продължителност (мин)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editCaloriesInput,
                        onValueChange = { editCaloriesInput = it },
                        label = { Text("Изгорени калории") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showEditDatePicker = true }
                    ) {
                        OutlinedTextField(
                            value = editDateInput,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Дата (ДД.ММ.ГГГГ)") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    if (showEditDatePicker) {
                        val calendar = Calendar.getInstance()
                        try {
                            calendar.time = dateFormat.parse(editDateInput) ?: Date()
                        } catch (_: Exception) {}

                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                calendar.set(year, month, dayOfMonth)
                                editDateInput = dateFormat.format(calendar.time)
                                showEditDatePicker = false
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            setOnCancelListener { showEditDatePicker = false }
                        }.show()
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val duration = editDurationInput.toIntOrNull() ?: workout.durationMinutes
                    val calories = editCaloriesInput.toIntOrNull() ?: workout.caloriesBurned
                    if (editTitleInput.isNotBlank()) {
                        viewModel.updateWorkout(
                            workout.copy(
                                title = editTitleInput,
                                durationMinutes = duration,
                                caloriesBurned = calories,
                                date = editDateInput
                            )
                        )
                        workoutToEdit = null
                    }
                }) {
                    Text("Обнови")
                }
            },
            dismissButton = {
                TextButton(onClick = { workoutToEdit = null }) {
                    Text("Отказ")
                }
            }
        )
    }
}