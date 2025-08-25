package com.example.myrecipebook.ui

import android.os.Bundle
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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    private val threadPool = Executors.newFixedThreadPool(10)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainThreadName = Thread.currentThread().name
        println("Метод onCreate() выполняется на потоке: $mainThreadName")

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

        Thread {
            try {
                val threadName = Thread.currentThread().name
                println("Выполняю запрос категорий на потоке: $threadName")

                val url = URL("https://recipes.androidsprint.ru/api/category")
                val connection = url.openConnection() as HttpURLConnection

                try {
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000
                    connection.setRequestProperty("Accept", "application/json")
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.connect()

                    val responseCode = connection.responseCode
                    println("Response Code: $responseCode")

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream = connection.inputStream
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val response = StringBuilder()
                        var line: String?

                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                        reader.close()

                        println("Successful API Response:")
                        println(response.toString())

                        try {
                            val categories =
                                json.decodeFromString<List<Category>>(response.toString())
                            println("Десериализация успешна!")
                            println("Получено категорий: ${categories.size}")

                            println("\nСПИСОК КАТЕГОРИЙ")
                            categories.forEachIndexed { index, category ->
                                println("Категория ${index + 1}:")
                                println("  ID: ${category.id}")
                                println("  Название: ${category.title}")
                                println("  Описание: ${category.description}")
                                println("  URL изображения: ${category.imageUrl}")
                                println()
                            }

                            val categoryIds = categories.map { it.id }
                            println("ID категорий для запроса рецептов: $categoryIds")

                            categories.forEach { category ->
                                threadPool.execute {
                                    extractRecipesCategory(category.id, category.title)
                                }
                            }

                            println("Все запросы рецептов отправлены в пул потоков")

                        } catch (e: Exception) {
                            println("Ошибка десериализации: ${e.message}")
                            e.printStackTrace()
                        }

                    } else {
                        val errorStream = connection.errorStream
                        if (errorStream != null) {
                            val errorReader = BufferedReader(InputStreamReader(errorStream))
                            val errorResponse = StringBuilder()
                            var errorLine: String?

                            while (errorReader.readLine().also { errorLine = it } != null) {
                                errorResponse.append(errorLine)
                            }
                            errorReader.close()
                            println("Error Response: $errorResponse")
                        }
                        println("Error Code: $responseCode")
                    }

                } finally {
                    connection.disconnect()
                }

            } catch (e: Exception) {
                println("Network request failed: ${e.message}")
                e.printStackTrace()
            }
        }.start()
    }

    private fun extractRecipesCategory(categoryId: Int, categoryTitle: String) {
        val threadName = Thread.currentThread().name
        println("\nЗАПРОС РЕЦЕПТОВ ДЛЯ КАТЕГОРИИ: $categoryTitle (ID: $categoryId)")
        println("Выполняется на потоке: $threadName")

        try {
            val url = URL("https://recipes.androidsprint.ru/api/category/${categoryId}/recipes")
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connect()

                val responseCode = connection.responseCode
                println("Response Code: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = StringBuilder()
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    try {
                        val recipes = json.decodeFromString<List<Recipe>>(response.toString())
                        println("ПОЛУЧЕНО РЕЦЕПТОВ: ${recipes.size}")

                        if (recipes.isNotEmpty()) {
                            println("\nСПИСОК РЕЦЕПТОВ:")
                            recipes.forEachIndexed { index, recipe ->
                                println("\nРЕЦЕПТ ${index + 1}")
                                println("ID: ${recipe.id}")
                                println("Название: ${recipe.title}")
                                println("URL изображения: ${recipe.imageUrl}")
                                println("Количество ингредиентов: ${recipe.ingredients.size}")
                                println("Количество шагов приготовления: ${recipe.method.size}")

                                if (recipe.ingredients.isNotEmpty()) {
                                    println("Ингредиенты:")
                                    recipe.ingredients.forEachIndexed { ingIndex, ingredient ->
                                        println("  ${ingIndex + 1}. ${ingredient.quantity} ${ingredient.unitOfMeasure} ${ingredient.description}")
                                    }
                                } else {
                                    println("Ингредиенты: нет ингредиентов")
                                }

                                if (recipe.method.isNotEmpty()) {
                                    println("Шаги приготовления:")
                                    recipe.method.forEachIndexed { stepIndex, step ->
                                        println("  ${stepIndex + 1}. $step")
                                    }
                                } else {
                                    println("Шаги приготовления: нет шагов")
                                }
                            }
                        } else {
                            println("В этой категории нет рецептов")
                        }

                    } catch (e: Exception) {
                        println("Ошибка десериализации рецептов: ${e.message}")
                        println(response.toString().take(500))
                        if (response.length > 500) println("...")
                    }

                } else {
                    println("ОШИБКА ДЛЯ КАТЕГОРИИ: $categoryTitle")
                    println("Код ошибки: $responseCode")

                    val errorStream = connection.errorStream
                    if (errorStream != null) {
                        val errorReader = BufferedReader(InputStreamReader(errorStream))
                        val errorResponse = StringBuilder()
                        var errorLine: String?

                        while (errorReader.readLine().also { errorLine = it } != null) {
                            errorResponse.append(errorLine)
                        }
                        errorReader.close()
                    }
                }

            } finally {
                connection.disconnect()
            }

        } catch (e: Exception) {
            println("СЕТЕВАЯ ОШИБКА ДЛЯ КАТЕГОРИИ: $categoryTitle")
            println("Сообщение: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        threadPool.shutdown()
    }
}