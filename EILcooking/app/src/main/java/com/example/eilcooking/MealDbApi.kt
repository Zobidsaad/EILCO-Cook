// MealDbApi.kt

package com.example.eilcooking

import retrofit2.http.GET

interface MealDbApi {
    @GET("categories.php")
    suspend fun getCategories(): CategoriesResponse // Remplacez CategoriesResponse par le type de réponse attendu
    // Définissez d'autres appels d'API ici
}


