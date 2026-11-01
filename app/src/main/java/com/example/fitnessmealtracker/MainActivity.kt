package com.example.fitnessmealtracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessmealtracker.data.local.AppDatabase
import com.example.fitnessmealtracker.data.repository.MealRepositoryImpl
import com.example.fitnessmealtracker.ui.dashboard.DashboardScreen
import com.example.fitnessmealtracker.ui.dashboard.DashboardViewModel
import com.example.fitnessmealtracker.ui.meals.MealViewModelFactory
import com.example.fitnessmealtracker.ui.meals.MealsScreen
import com.example.fitnessmealtracker.ui.meals.MealViewModel
import com.example.fitnessmealtracker.ui.theme.FitnessMealTrackerTheme
import com.example.fitnessmealtracker.ui.workouts.PedometerScreen
import com.example.fitnessmealtracker.ui.workouts.PedometerViewModel
import com.example.fitnessmealtracker.ui.workouts.StepHistoryScreen
import com.example.fitnessmealtracker.ui.workouts.WorkoutsScreen
import com.example.fitnessmealtracker.ui.workouts.WorkoutsViewModel
import com.example.fitnessmealtracker.ui.workouts.WorkoutsViewModelFactory
import com.example.fitnessmealtracker.data.repository.WorkoutRepositoryImpl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Състояние за глобално управление на тъмната/светлата тема
            var isDarkTheme by remember { mutableStateOf(true) }

            FitnessMealTrackerTheme(darkTheme = isDarkTheme) {
                val context = LocalContext.current

                // Автоматично изискване на разрешение за стъпките при стартиране
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { _ -> }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACTIVITY_RECOGNITION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                        }
                    }
                }

                var currentScreen by remember { mutableStateOf("dashboard") }

                val database = AppDatabase.getDatabase(applicationContext)

                val dashboardViewModel: DashboardViewModel = viewModel()
                val pedometerViewModel: PedometerViewModel = viewModel()

                val mealRepository = MealRepositoryImpl(database.mealDao())
                val mealViewModel: MealViewModel = viewModel(
                    factory = MealViewModelFactory(mealRepository)
                )

                val workoutRepository = WorkoutRepositoryImpl(database.workoutDao())
                val workoutsViewModel: WorkoutsViewModel = viewModel(
                    factory = WorkoutsViewModelFactory(workoutRepository)
                )

                Scaffold(
                    topBar = {
                        @OptIn(ExperimentalMaterial3Api::class)
                        TopAppBar(
                            title = { Text("Smart Fitness Tracker") },
                            actions = {
                                // Бутон за глобална смяна на цветовете (Тема: Тъмна / Светла)
                                IconButton(onClick = { isDarkTheme = !isDarkTheme }) {
                                    Text(
                                        text = if (isDarkTheme) "☀️" else "🌙",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            NavigationBarItem(
                                icon = { Text("🏠", style = MaterialTheme.typography.titleMedium) },
                                label = { Text("Табло") },
                                selected = currentScreen == "dashboard",
                                onClick = { currentScreen = "dashboard" }
                            )
                            NavigationBarItem(
                                icon = { Text("🍎", style = MaterialTheme.typography.titleMedium) },
                                label = { Text("Храна") },
                                selected = currentScreen == "meals",
                                onClick = { currentScreen = "meals" }
                            )
                            NavigationBarItem(
                                icon = { Text("💪", style = MaterialTheme.typography.titleMedium) },
                                label = { Text("Тренировки") },
                                selected = currentScreen == "workouts",
                                onClick = { currentScreen = "workouts" }
                            )
                            NavigationBarItem(
                                icon = { Text("👟", style = MaterialTheme.typography.titleMedium) },
                                label = { Text("Стъпки") },
                                selected = currentScreen == "pedometer",
                                onClick = { currentScreen = "pedometer" }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "dashboard" -> {
                                val meals by mealViewModel.allMeals.collectAsState(initial = emptyList())
                                val workouts by workoutsViewModel.allWorkouts.collectAsState(initial = emptyList())

                                DashboardScreen(
                                    dashboardViewModel = dashboardViewModel,
                                    pedometerViewModel = pedometerViewModel,
                                    meals = meals,
                                    workouts = workouts
                                )
                            }
                            "meals" -> MealsScreen(viewModel = mealViewModel)
                            "workouts" -> WorkoutsScreen(viewModel = workoutsViewModel)
                            "pedometer" -> {
                                var showHistory by remember { mutableStateOf(false) }
                                if (showHistory) {
                                    val history by pedometerViewModel.allStepHistory.collectAsState(initial = emptyList())
                                    StepHistoryScreen(
                                        stepHistory = history,
                                        onBack = { showHistory = false }
                                    )
                                } else {
                                    PedometerScreen(
                                        viewModel = pedometerViewModel,
                                        onNavigateToHistory = { showHistory = true }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}