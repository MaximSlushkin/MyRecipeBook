package com.example.myrecipebook.ui.recipes.recipelist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.model.Recipe
data class RecipesListState(
    val recipes: List<Recipe> = emptyList(),
    val categoryName: String = "",
    val categoryImageUrl: String? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableLiveData<RecipesListState>()
    val state: LiveData<RecipesListState> = _state

    private val repository = RecipeRepository()

    fun setCategory(id: Int?, name: String?, imageUrl: String?) {
        val fullImageUrl = if (!imageUrl.isNullOrEmpty()) {
            "https://recipes.androidsprint.ru/api/images/$imageUrl"
        } else {
            null
        }

        val currentState = _state.value ?: RecipesListState()
        _state.value = currentState.copy(
            categoryName = name ?: "",
            categoryImageUrl = fullImageUrl,
            isLoading = true
        )

        loadRecipes(id, name, fullImageUrl)
    }

    private fun loadRecipes(categoryId: Int?, categoryName: String?, categoryImageUrl: String?) {
        repository.getRecipesByCategory(categoryId ?: -1) { recipes ->
            if (recipes != null) {
                _state.postValue(
                    RecipesListState(
                        recipes = recipes,
                        categoryName = categoryName ?: "",
                        categoryImageUrl = categoryImageUrl,
                        isLoading = false,
                    )
                )
            } else {
                _state.postValue(
                    RecipesListState(
                        categoryName = categoryName ?: "",
                        categoryImageUrl = categoryImageUrl,
                        isLoading = false,
                        isError = true
                    )
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.shutdown()
    }
}