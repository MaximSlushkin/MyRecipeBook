package com.example.myrecipebook.ui.recipes.recipe

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myrecipebook.FAVORITES_KEY
import com.example.myrecipebook.PREFS_NAME
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.model.Recipe
import java.io.IOException

class RecipeViewModel(
    application: Application,
) : AndroidViewModel(application) {
    data class RecipeState(
        val recipe: Recipe? = null,
        val portionCount: Int = 1,
        val isFavorite: Boolean = false,
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val recipeImageUrl: String? = null,
    )

    private val sharedPref = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val repository = RecipeRepository()

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

        val favorites = getFavorites().toMutableSet()
        val recipeIdStr = currentRecipe.id.toString()

        val newFavoriteStatus = !currentState.isFavorite
        if (newFavoriteStatus) {
            favorites.add(recipeIdStr)
        } else {
            favorites.remove(recipeIdStr)
        }
        saveFavorites(favorites)

        updateState(currentState.copy(isFavorite = newFavoriteStatus))
    }

    private fun saveFavorites(favorites: Set<String>) {
        sharedPref.edit().putStringSet(FAVORITES_KEY, favorites).apply()
    }

    private fun getFavorites(): MutableSet<String> {
        val favorites = sharedPref.getStringSet(FAVORITES_KEY, null) ?: mutableSetOf()
        return HashSet(favorites)
    }

    fun updatePortionCount(newPortionCount: Int) {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(portionCount = newPortionCount)
    }

    fun loadRecipe(recipeId: Int) {
        _state.value = _state.value?.copy(isLoading = true) ?: RecipeState(isLoading = true)

        repository.getRecipeById(recipeId) { recipe ->
            if (recipe != null) {

                val imageUrl = "https://recipes.androidsprint.ru/api/images/${recipe.imageUrl}"

                val favorites = getFavorites()
                val isFavorite = favorites.contains(recipe.id.toString())
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
                val newState = _state.value?.copy(
                    isLoading = false,
                    isError = true,
                    recipeImageUrl = null,
                ) ?: RecipeState(
                    isLoading = false,
                    isError = true,
                    recipeImageUrl = null,
                )
                _state.postValue(newState)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.shutdown()
    }
}