package com.example.fitnessmealtracker.ui.meals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessmealtracker.data.local.entity.MealEntity
import com.example.fitnessmealtracker.util.ValidationUtils
import com.example.fitnessmealtracker.util.ValidationResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsScreen(viewModel: MealViewModel) {
    val meals by viewModel.allMeals.collectAsState()

    var isScanning by remember { mutableStateOf(false) }

    // Форма за въвеждане
    var nameInput by remember { mutableStateOf("") }
    var caloriesInput by remember { mutableStateOf("") }
    var proteinInput by remember { mutableStateOf("") }
    var carbsInput by remember { mutableStateOf("") }
    var fatInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf("13.10.2026") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var mealToEdit by remember { mutableStateOf<MealEntity?>(null) }
    var mealToViewDetails by remember { mutableStateOf<MealEntity?>(null) }

    if (isScanning) {
        BarcodeScannerScreen(
            onScanResult = { scannedBarcode ->
                isScanning = false
                viewModel.fetchProductInfo(scannedBarcode)
            },
            onBack = { isScanning = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Smart Fitness - Хранения") })
            }
        ) { paddingValues ->
            // Глобален скрол за целия екран
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { isScanning = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("📷 Сканирай баркод на продукт")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Име на храненето") },
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
                    label = { Text("Дата (ДД.ММ.ГГГГ)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val caloriesInt = caloriesInput.toIntOrNull() ?: -1
                        val proteinDouble = proteinInput.toDoubleOrNull() ?: -1.0
                        val carbsDouble = carbsInput.toDoubleOrNull() ?: -1.0
                        val fatDouble = fatInput.toDoubleOrNull() ?: -1.0

                        val validationResult = ValidationUtils.validateMeal(
                            name = nameInput,
                            calories = caloriesInt,
                            protein = proteinDouble,
                            date = dateInput
                        )

                        when (validationResult) {
                            is ValidationResult.Success -> {
                                errorMessage = null
                                viewModel.insertMeal(
                                    MealEntity(
                                        name = nameInput,
                                        calories = caloriesInt,
                                        protein = proteinDouble,
                                        carbs = carbsDouble,
                                        fat = fatDouble,
                                        date = dateInput
                                    )
                                )
                                nameInput = ""
                                caloriesInput = ""
                                proteinInput = ""
                                carbsInput = ""
                                fatInput = ""
                            }
                            is ValidationResult.Error -> {
                                errorMessage = validationResult.message
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Добави хранене")
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Списък с хранения:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                if (meals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Все още няма добавени хранения.")
                    }
                } else {
                    meals.forEach { meal ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { mealToViewDetails = meal }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = meal.name, style = MaterialTheme.typography.bodyLarge)
                                    Row {
                                        TextButton(onClick = { mealToEdit = meal }) {
                                            Text("Редактирай")
                                        }
                                        TextButton(onClick = { viewModel.deleteMeal(meal) }) {
                                            Text("Изтрий", color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                                Text(text = "Калории: ${meal.calories} kcal")
                                Text(text = "П: ${meal.protein}g | В: ${meal.carbs}g | М: ${meal.fat}g")
                                Text(text = "Дата: ${meal.date}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалози за API, Редакция и Детайли
    viewModel.scannedProduct?.let { product ->
        val name = product.productName ?: "Неизвестен продукт"
        val kcal = product.nutriments?.energyKcal100g?.toInt() ?: 0
        val proteins = product.nutriments?.proteins100g ?: 0.0
        val carbs = product.nutriments?.carbohydrates100g ?: 0.0
        val fat = product.nutriments?.fat100g ?: 0.0

        AlertDialog(
            onDismissRequest = { viewModel.clearScannedProduct() },
            title = { Text("Намерен продукт: $name") },
            text = {
                Column {
                    Text("Стойности на 100g:")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("🔥 Калории: $kcal kcal")
                    Text("🥩 Протеини: $proteins g")
                    Text("🍞 Въглехидрати: $carbs g")
                    Text("🥑 Мазнини: $fat g")
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.insertMeal(
                        MealEntity(
                            name = name,
                            calories = kcal,
                            protein = proteins,
                            carbs = carbs,
                            fat = fat,
                            date = dateInput
                        )
                    )
                    viewModel.clearScannedProduct()
                }) {
                    Text("Запиши в дневника")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.clearScannedProduct() }) {
                    Text("Отказ")
                }
            }
        )
    }

    mealToEdit?.let { meal ->
        AddEditMealDialog(
            meal = meal,
            onDismiss = { mealToEdit = null },
            onConfirm = { updatedMeal ->
                viewModel.updateMeal(updatedMeal)
                mealToEdit = null
            }
        )
    }

    mealToViewDetails?.let { meal ->
        AlertDialog(
            onDismissRequest = { mealToViewDetails = null },
            title = { Text(meal.name) },
            text = {
                Column {
                    Text("Калории: ${meal.calories} kcal")
                    Text("Протеини: ${meal.protein} g")
                    Text("Въглехидрати: ${meal.carbs} g")
                    Text("Мазнини: ${meal.fat} g")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Дата: ${meal.date}")
                }
            },
            confirmButton = {
                Button(onClick = { mealToViewDetails = null }) {
                    Text("Затвори")
                }
            }
        )
    }
}