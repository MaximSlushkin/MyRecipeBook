package com.example.myrecipebook.ui.recipes.recipelist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.model.Recipe
import kotlinx.coroutines.launch

data class RecipesListState(
    val recipes: List<Recipe> = emptyList(),
    val categoryName: String = "",
    val categoryImageUrl: String? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null
)

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableLiveData<RecipesListState>()
    val state: LiveData<RecipesListState> = _state

    private val _toastEvent = MutableLiveData<String?>()
    val toastEvent: LiveData<String?> = _toastEvent

    private val repository = RecipeRepository(application)
    private var currentCategoryId: Int? = null

    init {
        _state.value = RecipesListState()
    }

    fun setCategory(id: Int?, name: String?, imageUrl: String?) {
        currentCategoryId = id
        val fullImageUrl = if (!imageUrl.isNullOrEmpty()) {
            "https://recipes.androidsprint.ru/api/images/$imageUrl"
        } else {
            null
        }

        _state.value = RecipesListState(
            categoryName = name ?: "",
            categoryImageUrl = fullImageUrl,
            isLoading = true
        )

        loadRecipesWithCache(id, name, fullImageUrl)
    }

    private fun loadRecipesWithCache(
        categoryId: Int?,
        categoryName: String?,
        categoryImageUrl: String?
    ) {
        _state.value = RecipesListState(isLoading = true)

        viewModelScope.launch {
            try {

                val cachedRecipes = if (categoryId != null) {
                    repository.getRecipesFromCacheOnce(categoryId)
                } else {
                    emptyList()
                }

                if (cachedRecipes.isNotEmpty()) {
                    _state.value = RecipesListState(
                        recipes = cachedRecipes,
                        categoryName = categoryName ?: "",
                        categoryImageUrl = categoryImageUrl,
                        isLoading = true
                    )
                }

                val networkRecipes = if (categoryId != null) {
                    repository.getRecipesByCategory(categoryId)
                } else {
                    emptyList()
                }

                if (networkRecipes.isNotEmpty()) {
                    _state.value = RecipesListState(
                        recipes = networkRecipes,
                        categoryName = categoryName ?: "",
                        categoryImageUrl = categoryImageUrl,
                        isLoading = false
                    )
                } else if (cachedRecipes.isNotEmpty()) {
                    _state.value = RecipesListState(
                        recipes = cachedRecipes,
                        categoryName = categoryName ?: "",
                        categoryImageUrl = categoryImageUrl,
                        isLoading = false,
                        isError = true,
                        errorMessage = "Офлайн режим: данные могут быть устаревшими"
                    )
                    _toastEvent.value = "Офлайн режим: данные могут быть устаревшими"
                } else {
                    _state.value = RecipesListState(
                        categoryName = categoryName ?: "",
                        categoryImageUrl = categoryImageUrl,
                        isLoading = false,
                        isError = true,
                        errorMessage = "Нет данных"
                    )
                    _toastEvent.value = "Нет данных"
                }

            } catch (e: Exception) {
                try {
                    val cachedRecipes = if (categoryId != null) {
                        repository.getRecipesFromCacheOnce(categoryId)
                    } else {
                        emptyList()
                    }

                    if (cachedRecipes.isNotEmpty()) {
                        _state.value = RecipesListState(
                            recipes = cachedRecipes,
                            categoryName = categoryName ?: "",
                            categoryImageUrl = categoryImageUrl,
                            isLoading = false,
                            isError = true,
                            errorMessage = "Офлайн режим: данные могут быть устаревшими"
                        )
                        _toastEvent.value = "Офлайн режим: данные могут быть устаревшими"
                    } else {
                        _state.value = RecipesListState(
                            categoryName = categoryName ?: "",
                            categoryImageUrl = categoryImageUrl,
                            isLoading = false,
                            isError = true,
                            errorMessage = "Ошибка получения данных"
                        )
                        _toastEvent.value = "Ошибка получения данных"
                    }
                } catch (cacheException: Exception) {
                    _state.value = RecipesListState(
                        categoryName = categoryName ?: "",
                        categoryImageUrl = categoryImageUrl,
                        isLoading = false,
                        isError = true,
                        errorMessage = "Ошибка получения данных"
                    )
                    _toastEvent.value = "Ошибка получения данных"
                }
            }
        }
    }

    fun refreshRecipes() {
        currentCategoryId?.let { categoryId ->
            val currentState = _state.value ?: RecipesListState()
            loadRecipesWithCache(
                categoryId,
                currentState.categoryName,
                currentState.categoryImageUrl
            )
        }
    }

    fun clearToastEvent() {
        _toastEvent.value = null
    }
}