package com.example.fitnessmealtracker.data.remote

import com.example.fitnessmealtracker.data.remote.dto.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.fitnessmealtracker.data.remote.dto.ProductSearchResponse

interface OpenFoodFactsApi {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): ProductResponse


    @GET("cgi/search.pl?search_simple=1&action=process&json=1")
    suspend fun searchProducts(@Query("search_terms") query: String): ProductSearchResponse

    companion object {
        const val BASE_URL = "https://world.openfoodfacts.org/"
    }




}