package com.example.fitnessmealtracker.ui.meals

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
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
import com.example.fitnessmealtracker.data.local.entity.MealEntity
import com.example.fitnessmealtracker.data.remote.dto.ProductDto
import com.example.fitnessmealtracker.util.ValidationResult
import com.example.fitnessmealtracker.util.ValidationUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsScreen(viewModel: MealViewModel) {
    val context = LocalContext.current
    val meals by viewModel.allMeals.collectAsState()

    var isScanning by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Временен продукт за избор на грамове (било то от скенер или търсене)
    var selectedProductForGrams by remember { mutableStateOf<ProductDto?>(null) }

    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    var dateInput by remember { mutableStateOf(dateFormat.format(Date())) }

    var showDatePicker by remember { mutableStateOf(false) }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Бутони за добавяне: Скенер и Търсене по име
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isScanning = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("📷 Сканирай")
                    }
                    Button(
                        onClick = { showSearchDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("🔍 Търси име")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.mealNameInput,
                    onValueChange = { viewModel.mealNameInput = it },
                    label = { Text("Продукти") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.caloriesInput,
                    onValueChange = { viewModel.caloriesInput = it },
                    label = { Text("Калории") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.proteinInput,
                    onValueChange = { viewModel.proteinInput = it },
                    label = { Text("Протеини (g)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.carbsInput,
                    onValueChange = { viewModel.carbsInput = it },
                    label = { Text("Въглехидрати (g)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.fatInput,
                    onValueChange = { viewModel.fatInput = it },
                    label = { Text("Мазнини (g)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Бутон за отмяна на последно добавено действие (Undo)
                OutlinedButton(
                    onClick = { viewModel.undoLastAction() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("↩️ Изчисти последно добавен продукт")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Поле за дата с календар
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
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

                if (showDatePicker) {
                    val calendar = Calendar.getInstance()
                    try {
                        calendar.time = dateFormat.parse(dateInput) ?: Date()
                    } catch (_: Exception) {}

                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            dateInput = dateFormat.format(calendar.time)
                            showDatePicker = false
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).apply {
                        setOnCancelListener { showDatePicker = false }
                    }.show()
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val caloriesInt = viewModel.caloriesInput.toIntOrNull() ?: -1
                        val proteinDouble = viewModel.proteinInput.toDoubleOrNull() ?: -1.0
                        val carbsDouble = viewModel.carbsInput.toDoubleOrNull() ?: 0.0
                        val fatDouble = viewModel.fatInput.toDoubleOrNull() ?: 0.0

                        val validationResult = ValidationUtils.validateMeal(
                            name = viewModel.mealNameInput,
                            calories = caloriesInt,
                            protein = proteinDouble,
                            date = dateInput
                        )

                        when (validationResult) {
                            is ValidationResult.Success -> {
                                errorMessage = null
                                viewModel.insertMeal(
                                    MealEntity(
                                        name = viewModel.mealNameInput,
                                        calories = caloriesInt,
                                        protein = proteinDouble,
                                        carbs = carbsDouble,
                                        fat = fatDouble,
                                        date = dateInput
                                    )
                                )
                                viewModel.clearForm()
                            }
                            is ValidationResult.Error -> {
                                errorMessage = validationResult.message
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Добави хранене в дневника")
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Списък с хранения:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                if (meals.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Все още няма добавени хранения.")
                    }
                } else {
                    meals.forEach { meal ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { mealToViewDetails = meal },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = meal.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row {
                                        TextButton(
                                            onClick = { mealToEdit = meal },
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                                        ) {
                                            Text("✏️ Редактирай", style = MaterialTheme.typography.bodySmall)
                                        }
                                        TextButton(
                                            onClick = { viewModel.deleteMeal(meal) },
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                                        ) {
                                            Text("🗑️ Изтрий", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🔥 ${meal.calories} kcal",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = meal.date,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "П: ${meal.protein}g   |   В: ${meal.carbs}g   |   М: ${meal.fat}g",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Хващаме сканирания продукт и го пращаме към диалога за грамове
    LaunchedEffect(viewModel.scannedProduct) {
        viewModel.scannedProduct?.let {
            selectedProductForGrams = it
        }
    }

    // ДИАЛОГ ЗА ТЪРСЕНЕ ПО ИМЕ
    if (showSearchDialog) {
        AlertDialog(
            onDismissRequest = { showSearchDialog = false; viewModel.clearSearchResults() },
            title = { Text("Търсене на продукт по име") },
            text = {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Въведете име (напр. banana)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.searchProductsByName(searchQuery) }) {
                        Text("Търси")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (viewModel.isLoadingProduct) {
                        CircularProgressIndicator()
                    } else {
                        Column(modifier = Modifier.heightIn(max = 200.dp).verticalScroll(rememberScrollState())) {
                            viewModel.searchResults.forEach { product ->
                                val prodName = product.productName ?: "Неизвестен"
                                TextButton(onClick = {
                                    selectedProductForGrams = product
                                    showSearchDialog = false
                                    searchQuery = "" // Изчистване на полето за търсене след избор
                                    viewModel.clearSearchResults()
                                }) {
                                    Text(prodName)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showSearchDialog = false
                    searchQuery = ""
                    viewModel.clearSearchResults()
                }) {
                    Text("Затвори")
                }
            }
        )
    }

    // ДИАЛОГ ЗА ВЪВЕЖДАНЕ НА ГРАМАЖ
    selectedProductForGrams?.let { product ->
        val name = product.productName ?: "Неизвестен продукт"
        val baseKcal = product.nutriments?.energyKcal100g ?: 0.0
        val baseProteins = product.nutriments?.proteins100g ?: 0.0
        val baseCarbs = product.nutriments?.carbohydrates100g ?: 0.0
        val baseFat = product.nutriments?.fat100g ?: 0.0

        var gramsInput by remember { mutableStateOf("100") }

        AlertDialog(
            onDismissRequest = { selectedProductForGrams = null; viewModel.clearScannedProduct() },
            title = { Text("Продукт: $name") },
            text = {
                Column {
                    Text("Стойности за 100g:")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("🔥 Калории: $baseKcal kcal")
                    Text("🥩 Протеини: $baseProteins g")
                    Text("🍞 Въглехидрати: $baseCarbs g")
                    Text("🥑 Мазнини: $baseFat g")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = gramsInput,
                        onValueChange = { gramsInput = it },
                        label = { Text("Въведете грамаж (g)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val grams = gramsInput.toDoubleOrNull() ?: 100.0
                    viewModel.appendScannedProduct(
                        productName = name,
                        baseKcal = baseKcal,
                        baseProtein = baseProteins,
                        baseCarbs = baseCarbs,
                        baseFat = baseFat,
                        grams = grams
                    )
                    selectedProductForGrams = null
                    viewModel.clearScannedProduct()
                }) {
                    Text("Добави към формата")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProductForGrams = null; viewModel.clearScannedProduct() }) {
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
                Button(onClick = { mealToViewDetails = null }) { Text("Затвори") }
            }
        )
    }
}