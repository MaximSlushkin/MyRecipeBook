package com.example.myrecipebook.ui.recipes.recipe

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

data class RecipeState(
    val recipe: Recipe? = null,
    val portionCount: Int = 1,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val recipeImageUrl: String? = null,
)

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _state = MutableLiveData<RecipeState>()

    val state: LiveData<RecipeState> = _state

    init {
        Log.i("RecipeViewModel", "ViewModel initialized")
        _state.value = RecipeState()
    }

    fun updateState(newState: RecipeState) {
        _state.value = newState
    }

    fun onFavoritesClicked() {
        val currentState = _state.value ?: return
        val currentRecipe = currentState.recipe ?: return

        val newFavoriteStatus = !currentState.isFavorite

        viewModelScope.launch {
            try {

                repository.updateFavoriteStatus(currentRecipe.id, newFavoriteStatus)

                updateState(currentState.copy(isFavorite = newFavoriteStatus))
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error updating favorite status: ${e.message}")
            }
        }
    }

    fun updatePortionCount(newPortionCount: Int) {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(portionCount = newPortionCount)
    }

    fun loadRecipe(recipeId: Int) {
        _state.value = _state.value?.copy(isLoading = true) ?: RecipeState(isLoading = true)

        viewModelScope.launch {
            try {
                val recipe = repository.getRecipeById(recipeId)
                if (recipe != null) {
                    val imageUrl = "https://recipes.androidsprint.ru/api/images/${recipe.imageUrl}"

                    val isFavorite = recipe.isFavorite
                    val currentPortionCount = _state.value?.portionCount ?: 1

                    val newState = RecipeState(
                        recipe = recipe,
                        isFavorite = isFavorite,
                        portionCount = currentPortionCount,
                        isLoading = false,
                        recipeImageUrl = imageUrl,
                    )

                    _state.postValue(newState)
                } else {

                }
            } catch (e: Exception) {
            }
        }
    }
}