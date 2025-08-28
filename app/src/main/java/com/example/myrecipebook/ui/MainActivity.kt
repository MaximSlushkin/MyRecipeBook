package com.example.myrecipebook.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.findNavController
import com.example.myrecipebook.R
import com.example.myrecipebook.databinding.ActivityMainBinding
import com.example.myrecipebook.model.Category
import com.example.myrecipebook.model.Recipe
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    private val threadPool = Executors.newFixedThreadPool(10)

    private val okHttpClient: OkHttpClient by lazy {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainThreadName = Thread.currentThread().name
        Log.d(TAG, "Метод onCreate() выполняется на потоке: $mainThreadName")

        enableEdgeToEdge()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBarsInsets.top,
                bottom = systemBarsInsets.bottom
            )
            insets
        }

        binding.btnRecipes.setOnClickListener {
            findNavController(R.id.mainContainer).navigate(R.id.categoriesListFragment)
        }

        binding.btnFavorites.setOnClickListener {
            findNavController(R.id.mainContainer).navigate(R.id.favoritesFragment)
        }

        threadPool.execute {
            extractCategories()
        }
    }

    private fun extractCategories() {
        val threadName = Thread.currentThread().name
        Log.d(TAG, "Выполняю запрос категорий на потоке: $threadName")

        val request = Request.Builder()
            .url("https://recipes.androidsprint.ru/api/category")
            .get()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()

        try {

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {

                val responseBody = response.body.string()
                Log.d(TAG, "Successful API Response:")
                Log.d(TAG, responseBody)

                try {
                    val categories = json.decodeFromString<List<Category>>(responseBody)

                    Log.d(TAG, "Десериализация успешна!")
                    Log.d(TAG, "Получено категорий: ${categories.size}")

                    Log.d(TAG, "\nСПИСОК КАТЕГОРИЙ")
                    categories.forEachIndexed { index, category ->
                        Log.d(TAG, "Категория ${index + 1}:")
                        Log.d(TAG, "  ID: ${category.id}")
                        Log.d(TAG, "  Название: ${category.title}")
                        Log.d(TAG, "  Описание: ${category.description}")
                        Log.d(TAG, "  URL изображения: ${category.imageUrl}")
                        Log.d(TAG, "")
                    }

                    val categoryIds = categories.map { it.id }
                    Log.d(TAG, "ID категорий для запроса рецептов: $categoryIds")

                    categories.forEach { category ->
                        threadPool.execute {
                            extractRecipesCategory(category.id, category.title)
                        }
                    }

                    Log.d(TAG, "Все запросы рецептов отправлены в пул потоков")

                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка десериализации: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                Log.e(TAG, "Error Code: ${response.code}")
                Log.e(TAG, "Error Message: ${response.message}")

                val errorBody = response.body.string()
                Log.e(TAG, "Error Response: $errorBody")
            }

        } catch (e: IOException) {
            Log.e(TAG, "Network request failed: ${e.message}")
            e.printStackTrace()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun extractRecipesCategory(categoryId: Int, categoryTitle: String) {
        val threadName = Thread.currentThread().name
        Log.d(TAG, "ЗАПРОС РЕЦЕПТОВ ДЛЯ КАТЕГОРИИ: $categoryTitle (ID: $categoryId)")
        Log.d(TAG, "Выполняется на потоке: $threadName")

        // Создаем запрос
        val request = Request.Builder()
            .url("https://recipes.androidsprint.ru/api/category/${categoryId}/recipes")
            .get()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()

        try {

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {

                val responseBody = response.body.string()

                try {
                    val recipes = json.decodeFromString<List<Recipe>>(responseBody)

                    Log.d(TAG, "ПОЛУЧЕНО РЕЦЕПТОВ: ${recipes.size}")

                    if (recipes.isNotEmpty()) {
                        Log.d(TAG, "\nСПИСОК РЕЦЕПТОВ:")
                        recipes.forEachIndexed { index, recipe ->
                            Log.d(TAG, "\nРЕЦЕПТ ${index + 1}")
                            Log.d(TAG, "ID: ${recipe.id}")
                            Log.d(TAG, "Название: ${recipe.title}")
                            Log.d(TAG, "URL изображения: ${recipe.imageUrl}")
                            Log.d(TAG, "Количество ингредиентов: ${recipe.ingredients.size}")
                            Log.d(TAG, "Количество шагов приготовления: ${recipe.method.size}")

                            if (recipe.ingredients.isNotEmpty()) {
                                Log.d(TAG, "Ингредиенты:")
                                recipe.ingredients.forEachIndexed { ingIndex, ingredient ->
                                    Log.d(
                                        TAG,
                                        "  ${ingIndex + 1}. ${ingredient.quantity} ${ingredient.unitOfMeasure} ${ingredient.description}"
                                    )
                                }
                            } else {
                                Log.d(TAG, "Ингредиенты: нет ингредиентов")
                            }

                            if (recipe.method.isNotEmpty()) {
                                Log.d(TAG, "Шаги приготовления:")
                                recipe.method.forEachIndexed { stepIndex, step ->
                                    Log.d(TAG, "  ${stepIndex + 1}. $step")
                                }
                            } else {
                                Log.d(TAG, "Шаги приготовления: нет шагов")
                            }
                        }
                    } else {
                        Log.d(TAG, "В этой категории нет рецептов")
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка десериализации рецептов: ${e.message}")
                    Log.e(TAG, responseBody.take(500))
                    if (responseBody.length > 500) Log.e(TAG, "...")
                }

            } else {
                Log.e(TAG, "ОШИБКА ДЛЯ КАТЕГОРИИ: $categoryTitle")
                Log.e(TAG, "Код ошибки: ${response.code}")
                Log.e(TAG, "Сообщение ошибки: ${response.message}")

            }

        } catch (e: IOException) {
            Log.e(TAG, "СЕТЕВАЯ ОШИБКА ДЛЯ КАТЕГОРИИ: $categoryTitle")
            Log.e(TAG, "Сообщение: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        threadPool.shutdown()
    }
}