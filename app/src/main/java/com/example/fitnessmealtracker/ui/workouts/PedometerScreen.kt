package com.example.fitnessmealtracker.ui.workouts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedometerScreen(
    viewModel: PedometerViewModel,
    onNavigateToHistory: () -> Unit
) {
    val steps = viewModel.stepCount
    val isSensorAvailable = viewModel.isSensorAvailable

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("👟 Стъпкомер") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isSensorAvailable) {
                Text(
                    text = "Хардуерен сензор за стъпки не е наличен.",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(text = "Изминати стъпки днес:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "$steps", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onNavigateToHistory) {
                Text("📜 Виж история по дни")
            }
        }
    }
}