package com.example.fitnessmealtracker.ui.meals

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessmealtracker.data.local.entity.MealEntity

@Composable
fun AddEditMealDialog(
    meal: MealEntity,
    onDismiss: () -> Unit,
    onConfirm: (MealEntity) -> Unit
) {
    var nameInput by remember { mutableStateOf(meal.name) }
    var caloriesInput by remember { mutableStateOf(if (meal.calories > 0) meal.calories.toString() else "") }
    var proteinInput by remember { mutableStateOf(if (meal.protein > 0) meal.protein.toString() else "") }
    var carbsInput by remember { mutableStateOf(if (meal.carbs > 0) meal.carbs.toString() else "") }
    var fatInput by remember { mutableStateOf(if (meal.fat > 0) meal.fat.toString() else "") }
    var dateInput by remember { mutableStateOf(meal.date) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактиране на хранене") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Име") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = caloriesInput,
                    onValueChange = { caloriesInput = it },
                    label = { Text("Калории") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = proteinInput,
                    onValueChange = { proteinInput = it },
                    label = { Text("Протеини (g)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = carbsInput,
                    onValueChange = { carbsInput = it },
                    label = { Text("Въглехидрати (g)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fatInput,
                    onValueChange = { fatInput = it },
                    label = { Text("Мазнини (g)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { dateInput = it },
                    label = { Text("Дата") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedMeal = meal.copy(
                        name = nameInput,
                        calories = caloriesInput.toIntOrNull() ?: meal.calories,
                        protein = proteinInput.toDoubleOrNull() ?: meal.protein,
                        carbs = carbsInput.toDoubleOrNull() ?: meal.carbs,
                        fat = fatInput.toDoubleOrNull() ?: meal.fat,
                        date = dateInput
                    )
                    onConfirm(updatedMeal)
                }
            ) {
                Text("Запази")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отказ")
            }
        }
    )
}
