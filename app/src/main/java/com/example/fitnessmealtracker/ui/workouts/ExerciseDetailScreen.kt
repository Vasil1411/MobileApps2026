package com.example.fitnessmealtracker.ui.workouts

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessmealtracker.data.local.entity.ExerciseEntity
import com.example.fitnessmealtracker.data.local.entity.WorkoutEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    workout: WorkoutEntity,
    viewModel: WorkoutsViewModel,
    onBack: () -> Unit
) {
    val exercises by viewModel.getExercisesForWorkout(workout.id).collectAsState(initial = emptyList())

    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var exerciseToEdit by remember { mutableStateOf<ExerciseEntity?>(null) } // Задържа упражнението, което се редактира

    var exerciseNameInput by remember { mutableStateOf("") }
    var setsInput by remember { mutableStateOf("") }
    var repsInput by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    BackHandler() {
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Упражнения: ${workout.title}") },

            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                exerciseToEdit = null
                exerciseNameInput = ""
                setsInput = ""
                repsInput = ""
                weightInput = ""
                errorMessage = null
                showAddExerciseDialog = true
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
            Text(text = "📅 Дата: ${workout.date} | ⏱ ${workout.durationMinutes} мин", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Списък с упражнения:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (exercises.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Все още няма добавени упражнения. Натисни '+' долу вдясно.")
                }
            } else {
                exercises.forEach { exercise ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = exercise.name, style = MaterialTheme.typography.bodyLarge)
                                Text(text = "Серии: ${exercise.sets} | Повторения: ${exercise.reps} | Тежест: ${exercise.weight} kg")
                            }

                            Row {
                                TextButton(onClick = {
                                    exerciseToEdit = exercise
                                    exerciseNameInput = exercise.name
                                    setsInput = exercise.sets.toString()
                                    repsInput = exercise.reps.toString()
                                    weightInput = exercise.weight.toString()
                                    errorMessage = null
                                    showAddExerciseDialog = true
                                }) {
                                    Text("Редактирай", color = MaterialTheme.colorScheme.primary)
                                }
                                TextButton(onClick = { viewModel.deleteExercise(exercise) }) {
                                    Text("Изтрий", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог за добавяне или редактиране на упражнение
    if (showAddExerciseDialog) {
        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            title = { Text(if (exerciseToEdit == null) "Ново упражнение" else "Редактирай упражнение") },
            text = {
                Column {
                    OutlinedTextField(
                        value = exerciseNameInput,
                        onValueChange = { exerciseNameInput = it },
                        label = { Text("Име (напр. Клек, Бенч прес)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = setsInput,
                        onValueChange = { setsInput = it },
                        label = { Text("Серии (напр. 3)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = repsInput,
                        onValueChange = { repsInput = it },
                        label = { Text("Повторения (напр. 10)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Тежест в кг (напр. 50.0)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val sets = setsInput.toIntOrNull()
                    val reps = repsInput.toIntOrNull()
                    val weight = weightInput.toDoubleOrNull()

                    if (exerciseNameInput.isBlank() || sets == null || sets <= 0 || reps == null || reps <= 0 || weight == null || weight < 0) {
                        errorMessage = "Моля, попълнете коректно всички полета!"
                    } else {
                        errorMessage = null
                        if (exerciseToEdit == null) {
                            viewModel.insertExercise(
                                ExerciseEntity(
                                    workoutId = workout.id,
                                    name = exerciseNameInput,
                                    sets = sets,
                                    reps = reps,
                                    weight = weight
                                )
                            )
                        } else {
                            viewModel.updateExercise(
                                exerciseToEdit!!.copy(
                                    name = exerciseNameInput,
                                    sets = sets,
                                    reps = reps,
                                    weight = weight
                                )
                            )
                        }
                        showAddExerciseDialog = false
                    }
                }) {
                    Text(if (exerciseToEdit == null) "Добави" else "Запази")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) {
                    Text("Отказ")
                }
            }
        )
    }
}