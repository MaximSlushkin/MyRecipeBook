package com.example.myrecipebook.ui.recipes.recipe

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myrecipebook.FAVORITES_KEY
import com.example.myrecipebook.PREFS_NAME
import com.example.myrecipebook.data.STUB
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
        val error: Throwable? = null,
        val recipeImage: Drawable? = null,
    )

    private val sharedPref = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _state = MutableLiveData<RecipeState>()

    val state: LiveData<RecipeState> = _state

    init {
        Log.i("RecipeViewModel", "ViewModel initialized")
        _state.value = RecipeState(isFavorite = true)
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

    fun loadRecipe(recipeId: Int) {
        _state.value = _state.value?.copy(isLoading = true) ?: RecipeState(isLoading = true)
        // TODO: 'Load from network'
        val recipe = STUB.getRecipeById(recipeId)

        if (recipe != null) {
            val drawable: Drawable? =
                try {
                    // [CHANGED] Загрузка изображения перемещена из фрагмента сюда
                    val inputStream = getApplication<Application>().assets.open(recipe.imageUrl)
                    Drawable.createFromStream(inputStream, null)
                } catch (e: IOException) {
                    Log.e("RecipeViewModel", "Error loading recipe image: ${recipe.imageUrl}", e)
                    null
                }

            val favorites = getFavorites()
            val isFavorite = favorites.contains(recipe.id.toString())

            val currentPortionCount = _state.value?.portionCount ?: 1

            val newState =
                _state.value?.copy(
                    recipe = recipe,
                    isFavorite = isFavorite,
                    portionCount = currentPortionCount,
                    isLoading = false,
                    recipeImage = drawable,
                ) ?: RecipeState(
                    recipe = recipe,
                    isFavorite = isFavorite,
                    portionCount = currentPortionCount,
                    isLoading = false,
                    recipeImage = drawable,
                )

            updateState(newState)
        } else {
            val newState =
                _state.value?.copy(
                    error = Throwable("Recipe not found"),
                    isLoading = false,
                    recipeImage = null,
                ) ?: RecipeState(
                    error = Throwable("Recipe not found"),
                    isLoading = false,
                    recipeImage = null,
                )

            updateState(newState)
        }
    }
}
