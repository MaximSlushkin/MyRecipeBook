package com.example.myrecipebook.di

import android.app.Application
import com.example.myrecipebook.data.network.RecipeApiService
import com.example.myrecipebook.data.network.RecipeDatabase
import com.example.myrecipebook.data.repository.RecipeRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class AppContainer(application: Application) {
    private val database by lazy {
        RecipeDatabase.getDatabase(application)
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
        val url = "https://recipes.androidsprint.ru/api/"
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    private val apiService: RecipeApiService by lazy {
        retrofit.create(RecipeApiService::class.java)
    }

    val recipeRepository: RecipeRepository by lazy {
        RecipeRepository(application, database, apiService)
    }
}