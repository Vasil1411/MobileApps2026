package com.example.fitnessmealtracker.ui.meals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitnessmealtracker.data.local.entity.MealEntity
import com.example.fitnessmealtracker.data.remote.OpenFoodFactsApi
import com.example.fitnessmealtracker.data.remote.dto.ProductDto
import com.example.fitnessmealtracker.data.repository.MealRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay



data class AddedItemRecord(
    val name: String,
    val grams: Double,
    val kcal: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)

class MealViewModel(private val repository: MealRepositoryImpl) : ViewModel() {

    val allMeals: StateFlow<List<MealEntity>> = repository.allMeals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- ФОРМУЛЯР ЗА ХРАНЕНЕ И ИСТОРИЯ ЗА UNDO ---
    var mealNameInput by mutableStateOf("")
    var caloriesInput by mutableStateOf("")
    var proteinInput by mutableStateOf("")
    var carbsInput by mutableStateOf("")
    var fatInput by mutableStateOf("")

    private val addedItemsHistory = mutableStateListOf<AddedItemRecord>()

    fun appendScannedProduct(productName: String, baseKcal: Double, baseProtein: Double, baseCarbs: Double, baseFat: Double, grams: Double) {
        val multiplier = grams / 100.0
        val addedKcal = (baseKcal * multiplier).toInt()
        val addedProtein = baseProtein * multiplier
        val addedCarbs = baseCarbs * multiplier
        val addedFat = baseFat * multiplier

        addedItemsHistory.add(
            AddedItemRecord(productName, grams, addedKcal, addedProtein, addedCarbs, addedFat)
        )
        rebuildFormFromHistory()
    }

    // Изчистване на последното добавено действие (Undo)
    fun undoLastAction() {
        if (addedItemsHistory.isNotEmpty()) {
            addedItemsHistory.removeAt(addedItemsHistory.lastIndex)
            rebuildFormFromHistory()
        }
    }

    private fun rebuildFormFromHistory() {
        if (addedItemsHistory.isEmpty()) {
            clearForm()
            return
        }

        mealNameInput = addedItemsHistory.joinToString(", ") { "${it.name} (${it.grams.toInt()}g)" }
        caloriesInput = addedItemsHistory.sumOf { it.kcal }.toString()
        proteinInput = String.format(Locale.getDefault(), "%.1f", addedItemsHistory.sumOf { it.protein })
        carbsInput = String.format(Locale.getDefault(), "%.1f", addedItemsHistory.sumOf { it.carbs })
        fatInput = String.format(Locale.getDefault(), "%.1f", addedItemsHistory.sumOf { it.fat })
    }

    fun clearForm() {
        addedItemsHistory.clear()
        mealNameInput = ""
        caloriesInput = ""
        proteinInput = ""
        carbsInput = ""
        fatInput = ""
    }

    // --- СКЕНЕР И ТЪРСЕНЕ ПО ИМЕ ---
    var scannedProduct by mutableStateOf<ProductDto?>(null)
        private set

    var searchResults by mutableStateOf<List<ProductDto>>(emptyList())
        private set

    var isLoadingProduct by mutableStateOf(false)
        private set

    private val retrofit = Retrofit.Builder()
        .baseUrl(OpenFoodFactsApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(OpenFoodFactsApi::class.java)

    fun fetchProductInfo(barcode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoadingProduct = true
            try {
                val response = api.getProductByBarcode(barcode)
                scannedProduct = response.product
            } catch (e: Exception) {
                e.printStackTrace()
                scannedProduct = null
            } finally {
                isLoadingProduct = false
            }
        }
    }

    private var searchJob: Job? = null
    fun searchProductsByName(query: String) {
        if (query.isBlank()) {
            searchResults = emptyList()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            isLoadingProduct = true
            var success = false
            var attempts = 0
            var results: List<ProductDto> = emptyList()

            // Опитваме до 3 пъти автоматично, ако сървърът бави отговор
            while (!success && attempts < 3) {
                attempts++
                try {
                    val response = api.searchProducts(query)
                    results = response.products ?: emptyList()
                    if (results.isNotEmpty()) {
                        success = true // Намерихме продукти, спираме цикъла
                    } else {
                        kotlinx.coroutines.delay(1000) // Изчакваме 1 секунда преди следващ опит
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (attempts >= 3) {
                        results = emptyList()
                    } else {
                        kotlinx.coroutines.delay(1000)
                    }
                }
            }

            searchResults = results
            isLoadingProduct = false
        }
    }

    fun clearScannedProduct() {
        scannedProduct = null
    }

    fun clearSearchResults() {
        searchResults = emptyList()
    }

    fun insertMeal(meal: MealEntity) {
        viewModelScope.launch { repository.insertMeal(meal) }
    }

    fun updateMeal(meal: MealEntity) {
        viewModelScope.launch { repository.updateMeal(meal) }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch { repository.deleteMeal(meal) }
    }
}

class MealViewModelFactory(private val repository: MealRepositoryImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}