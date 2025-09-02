package com.example.myrecipebook.data.repository

import android.util.Log
import com.example.myrecipebook.data.network.RecipeApiService
import com.example.myrecipebook.model.Category
import com.example.myrecipebook.model.Recipe
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.Response
import java.util.concurrent.Executors
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

    private val executorService = Executors.newFixedThreadPool(4)

    fun getCategories(callback: (List<Category>?) -> Unit) {
        executorService.execute {
            try {
                val response: Response<List<Category>> = apiService.getCategories().execute()
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    Log.d(TAG, "Successfully loaded ${categories.size} categories")
                    callback(categories)
                } else {
                    Log.e(TAG, "Error loading categories: ${response.code()} - ${response.message()}")
                    callback(null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading categories: ${e.message}")
                callback(null)
            }
        }
    }

    fun getRecipesByCategory(categoryId: Int, callback: (List<Recipe>?) -> Unit) {
        executorService.execute {
            try {
                val response: Response<List<Recipe>> = apiService.getRecipesByCategory(categoryId).execute()
                if (response.isSuccessful) {
                    val recipes = response.body() ?: emptyList()
                    Log.d(TAG, "Successfully loaded ${recipes.size} recipes for category $categoryId")
                    callback(recipes)
                } else {
                    Log.e(TAG, "Error loading recipes for category $categoryId: ${response.code()} - ${response.message()}")
                    callback(null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading recipes for category $categoryId: ${e.message}")
                callback(null)
            }
        }
    }

    fun getRecipeById(recipeId: Int, callback: (Recipe?) -> Unit) {
        executorService.execute {
            try {
                val response: Response<Recipe> = apiService.getRecipeById(recipeId).execute()
                if (response.isSuccessful) {
                    val recipe = response.body()
                    Log.d(TAG, "Successfully loaded recipe $recipeId")
                    callback(recipe)
                } else {
                    Log.e(TAG, "Error loading recipe $recipeId: ${response.code()} - ${response.message()}")
                    callback(null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading recipe $recipeId: ${e.message}")
                callback(null)
            }
        }
    }

    fun getRecipesByIds(ids: Set<Int>, callback: (List<Recipe>?) -> Unit) {
        executorService.execute {
            try {
                val recipes = mutableListOf<Recipe>()
                ids.forEach { id ->
                    val response: Response<Recipe> = apiService.getRecipeById(id).execute()
                    if (response.isSuccessful) {
                        response.body()?.let { recipes.add(it) }
                    }
                }
                Log.d(TAG, "Successfully loaded ${recipes.size} recipes from ${ids.size} IDs")
                callback(recipes)
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading recipes by IDs: ${e.message}")
                callback(null)
            }
        }
    }

    fun shutdown() {
        executorService.shutdown()
    }
}