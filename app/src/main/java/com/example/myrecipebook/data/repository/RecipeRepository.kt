package com.example.myrecipebook.data.repository

import android.util.Log
import com.example.myrecipebook.data.network.RecipeApiService
import com.example.myrecipebook.model.Category
import com.example.myrecipebook.model.Recipe
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.Response
import java.util.concurrent.TimeUnit

class RecipeRepository {

    private val URL = "https://recipes.androidsprint.ru/api/"
    private val TAG = "RecipeRepository"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    private val apiService: RecipeApiService by lazy {
        retrofit.create(RecipeApiService::class.java)
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    suspend fun getCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<List<Category>> = apiService.getCategories().execute()
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    Log.d(TAG, "Successfully loaded ${categories.size} categories")
                    categories
                } else {
                    Log.e(TAG, "Error loading categories: ${response.code()} - ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading categories: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun getRecipesByCategory(categoryId: Int): List<Recipe> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<List<Recipe>> = apiService.getRecipesByCategory(categoryId).execute()
                if (response.isSuccessful) {
                    val recipes = response.body() ?: emptyList()
                    Log.d(TAG, "Successfully loaded ${recipes.size} recipes for category $categoryId")
                    recipes
                } else {
                    Log.e(TAG, "Error loading recipes for category $categoryId: ${response.code()} - ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading recipes for category $categoryId: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun getRecipeById(recipeId: Int): Recipe? {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<Recipe> = apiService.getRecipeById(recipeId).execute()
                if (response.isSuccessful) {
                    val recipe = response.body()
                    Log.d(TAG, "Successfully loaded recipe $recipeId")
                    recipe
                } else {
                    Log.e(TAG, "Error loading recipe $recipeId: ${response.code()} - ${response.message()}")
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading recipe $recipeId: ${e.message}")
                null
            }
        }
    }

    suspend fun getRecipesByIds(ids: Set<Int>): List<Recipe> {
        return withContext(Dispatchers.IO) {
            try {
                val recipes = mutableListOf<Recipe>()
                ids.forEach { id ->
                    val response: Response<Recipe> = apiService.getRecipeById(id).execute()
                    if (response.isSuccessful) {
                        response.body()?.let { recipes.add(it) }
                    }
                }
                Log.d(TAG, "Successfully loaded ${recipes.size} recipes from ${ids.size} IDs")
                recipes
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading recipes by IDs: ${e.message}")
                emptyList()
            }
        }
    }
}