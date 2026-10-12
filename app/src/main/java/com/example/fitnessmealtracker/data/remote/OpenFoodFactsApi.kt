package com.example.fitnessmealtracker.data.remote

import com.example.fitnessmealtracker.data.remote.dto.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsApi {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): ProductResponse

    companion object {
        const val BASE_URL = "https://world.openfoodfacts.org/"
    }
}