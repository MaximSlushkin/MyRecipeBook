package com.example.myrecipebook.data.repository

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.myrecipebook.data.network.RecipeApiService
import com.example.myrecipebook.data.network.RecipeDatabase
import com.example.myrecipebook.model.Category
import com.example.myrecipebook.model.Recipe
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.Response
import java.util.concurrent.TimeUnit

class RecipeRepository(
    context: Context,
    private val database: RecipeDatabase,
    private val apiService: RecipeApiService
) {

    private val URL = "https://recipes.androidsprint.ru/api/"
    private val TAG = "RecipeRepository"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val categoriesDao by lazy {
        database.categoriesDao()
    }

    private val recipesDao by lazy {
        database.recipesDao()
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    fun getCategoriesFromCache(): Flow<List<Category>> {
        Log.d(TAG, "Getting categories from cache (database) - Flow")
        return categoriesDao.getAllCategories()
    }

    suspend fun getCategoriesFromCacheOnce(): List<Category> {
        Log.d(TAG, "Getting categories from cache (database) - one time request")
        return withContext(Dispatchers.IO) {
            try {
                categoriesDao.getAllCategoriesOnce()
            } catch (e: Exception) {
                Log.e(TAG, "Error getting categories from cache: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun getCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<List<Category>> = apiService.getCategories().execute()
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    Log.d(TAG, "Successfully loaded ${categories.size} categories")

                    val sortedCategories = categories.sortedBy { it.title }
                    categoriesDao.insertCategories(sortedCategories)

                    sortedCategories
                } else {
                    Log.e(
                        TAG,
                        "Error loading categories: ${response.code()} - ${response.message()}"
                    )

                    categoriesDao.getAllCategoriesOnce()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading categories: ${e.message}")

                categoriesDao.getAllCategoriesOnce()
            }
        }
    }

    fun getRecipesFromCache(categoryId: Int): Flow<List<Recipe>> {
        Log.d(TAG, "Getting recipes for category $categoryId from cache (database) - Flow")
        return recipesDao.getRecipesByCategory(categoryId)
    }

    suspend fun getRecipesFromCacheOnce(categoryId: Int): List<Recipe> {
        Log.d(
            TAG,
            "Getting recipes for category $categoryId from cache (database) - one time request"
        )
        return withContext(Dispatchers.IO) {
            try {
                recipesDao.getRecipesByCategoryOnce(categoryId)
            } catch (e: Exception) {
                Log.e(TAG, "Error getting recipes from cache: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun getRecipesByCategory(categoryId: Int): List<Recipe> {
        return withContext(Dispatchers.IO) {
            try {

                val favoriteRecipeIds = recipesDao.getFavoriteRecipesOnce()
                    .filter { it.categoryId == categoryId }
                    .map { it.id }
                    .toSet()

                val response: Response<List<Recipe>> =
                    apiService.getRecipesByCategory(categoryId).execute()
                if (response.isSuccessful) {
                    val recipes = response.body() ?: emptyList()
                    Log.d(
                        TAG,
                        "Successfully loaded ${recipes.size} recipes for category $categoryId"
                    )

                    val recipesWithCategoryAndFavorite = recipes.map {
                        it.copy(
                            categoryId = categoryId,
                            isFavorite = it.id in favoriteRecipeIds
                        )
                    }

                    val sortedRecipes = recipesWithCategoryAndFavorite.sortedBy { it.title }
                    recipesDao.insertRecipes(sortedRecipes)

                    sortedRecipes
                } else {
                    Log.e(
                        TAG,
                        "Error loading recipes for category $categoryId: ${response.code()} - ${response.message()}"
                    )
                    recipesDao.getRecipesByCategoryOnce(categoryId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading recipes for category $categoryId: ${e.message}")
                recipesDao.getRecipesByCategoryOnce(categoryId)
            }
        }
    }

    suspend fun getRecipeById(recipeId: Int): Recipe? {
        return withContext(Dispatchers.IO) {
            try {

                val cachedRecipe = recipesDao.getRecipeById(recipeId)
                if (cachedRecipe != null) {
                    Log.d(TAG, "Recipe $recipeId found in cache")
                    return@withContext cachedRecipe
                }

                val response: Response<Recipe> = apiService.getRecipeById(recipeId).execute()
                if (response.isSuccessful) {
                    val recipe = response.body()
                    Log.d(TAG, "Successfully loaded recipe $recipeId from server")
                    recipe?.let { recipesDao.insertRecipe(it) }
                    recipe
                } else {
                    Log.e(
                        TAG,
                        "Error loading recipe $recipeId: ${response.code()} - ${response.message()}"
                    )
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
                        response.body()?.let {
                            recipes.add(it)
                            recipesDao.insertRecipe(it)
                        }
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

    fun getFavoriteRecipesFromCache(): Flow<List<Recipe>> {
        Log.d(TAG, "Getting favorite recipes from cache (database) - Flow")
        return recipesDao.getFavoriteRecipes()
    }

    suspend fun getFavoriteRecipesFromCacheOnce(): List<Recipe> {
        Log.d(TAG, "Getting favorite recipes from cache (database) - one time request")
        return withContext(Dispatchers.IO) {
            try {
                recipesDao.getFavoriteRecipesOnce()
            } catch (e: Exception) {
                Log.e(TAG, "Error getting favorite recipes from cache: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun updateFavoriteStatus(recipeId: Int, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                recipesDao.updateFavoriteStatus(recipeId, isFavorite)
                Log.d(TAG, "Updated favorite status for recipe $recipeId: $isFavorite")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating favorite status: ${e.message}")
            }
        }
    }

    suspend fun getFavoritesCount(): Int {
        return withContext(Dispatchers.IO) {
            try {
                recipesDao.getFavoritesCount()
            } catch (e: Exception) {
                Log.e(TAG, "Error getting favorites count: ${e.message}")
                0
            }
        }
    }
}