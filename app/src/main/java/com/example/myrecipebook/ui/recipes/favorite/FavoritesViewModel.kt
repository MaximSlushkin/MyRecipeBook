package com.example.myrecipebook.ui.recipes.favorite

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myrecipebook.FAVORITES_KEY
import com.example.myrecipebook.PREFS_NAME
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.model.Recipe
import kotlinx.coroutines.launch

data class FavoritesState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    private val sharedPref = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val repository = RecipeRepository()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        _state.value = FavoritesState(isLoading = true)

        viewModelScope.launch {
            try {
                val favorites = getFavorites()
                val favoriteIds = favorites.map { it.toInt() }.toSet()

                if (favoriteIds.isEmpty()) {
                    _state.postValue(FavoritesState(recipes = emptyList(), isLoading = false))
                    return@launch
                }

                val recipes = repository.getRecipesByIds(favoriteIds)
                _state.postValue(FavoritesState(recipes = recipes, isLoading = false))
            } catch (e: Exception) {
                _state.postValue(FavoritesState(isLoading = false, isError = true))
            }
        }
    }

    private fun getFavorites(): Set<String> {
        return sharedPref.getStringSet(FAVORITES_KEY, null) ?: setOf()
    }
}