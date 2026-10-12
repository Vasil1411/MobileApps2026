package com.example.fitnessmealtracker.data.remote.dto


import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("product")
    val product: ProductDto?
)

data class ProductDto(
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("nutriments")
    val nutriments: NutrimentsDto?
)

data class NutrimentsDto(
    @SerializedName("energy-kcal_100g")
    val energyKcal100g: Double?,
    @SerializedName("proteins_100g")
    val proteins100g: Double?,
    @SerializedName("carbohydrates_100g")
    val carbohydrates100g: Double?,
    @SerializedName("fat_100g")
    val fat100g: Double?
)