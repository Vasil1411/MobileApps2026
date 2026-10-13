package com.example.fitnessmealtracker.ui.meals

import androidx.compose.runtime.getValue
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

class MealViewModel(private val repository: MealRepositoryImpl) : ViewModel() {

    val allMeals: StateFlow<List<MealEntity>> = repository.allMeals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- НОВИ СЪСТОЯНИЯ ЗА СКЕНЕРА И API-ТО ---
    var scannedProduct by mutableStateOf<ProductDto?>(null)
        private set

    var isLoadingProduct by mutableStateOf(false)
        private set

    // Инициализация на Retrofit за Open Food Facts
    private val retrofit = Retrofit.Builder()
        .baseUrl(OpenFoodFactsApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(OpenFoodFactsApi::class.java)

    // Функция за извличане на продукт по баркод от мрежата
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

    // Изчистване на сканирания продукт при затваряне на диалога
    fun clearScannedProduct() {
        scannedProduct = null
    }
    // ------------------------------------------

    fun insertMeal(meal: MealEntity) {
        viewModelScope.launch {
            repository.insertMeal(meal)
        }
    }

    fun updateMeal(meal: MealEntity) {
        viewModelScope.launch {
            repository.updateMeal(meal)
        }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
        }
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