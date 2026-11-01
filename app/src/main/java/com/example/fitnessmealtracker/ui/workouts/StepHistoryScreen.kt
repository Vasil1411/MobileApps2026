package com.example.fitnessmealtracker.ui.workouts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessmealtracker.data.local.entity.DailyStepEntity
import androidx.activity.compose.BackHandler


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepHistoryScreen(
    stepHistory: List<DailyStepEntity>,
    onBack: () -> Unit
) {


    BackHandler() {

        onBack()

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("👟 История на стъпките") },

            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Архив по дни",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (stepHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Все още няма записана история за минали дни.")
                }
            } else {
                stepHistory.forEach { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "📅 ${record.date}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Text(
                                text = "👟 ${record.steps} стъпки",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}