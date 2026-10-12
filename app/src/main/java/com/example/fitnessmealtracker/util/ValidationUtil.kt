package com.example.fitnessmealtracker.util

object ValidationUtils {


    fun validateMeal(name: String, calories: Int, protein: Double, date: String): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult.Error("Името на храненето не може да бъде празно.")
        }
        if (calories < 0) {
            return ValidationResult.Error("Калориите не могат да бъдат отрицателно число.")
        }
        if (protein < 0.0) {
            return ValidationResult.Error("Протеините не могат да бъдат отрицателни.")
        }
        if (!validateDateString(date)) {
            return ValidationResult.Error("Невалиден формат на дата (очакван: ДД.ММ.ГГГГ или ГГГГ-ММ-ДД).")
        }
        return ValidationResult.Success
    }


    fun validateWorkout(title: String, date: String): ValidationResult {
        if (title.isBlank()) {
            return ValidationResult.Error("Заглавието на тренировката е задължително.")
        }
        if (!validateDateString(date)) {
            return ValidationResult.Error("Невалиден формат на дата.")
        }
        return ValidationResult.Success
    }


    private fun validateDateString(date: String): Boolean {
        return date.isNotBlank() && date.length >= 8
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}