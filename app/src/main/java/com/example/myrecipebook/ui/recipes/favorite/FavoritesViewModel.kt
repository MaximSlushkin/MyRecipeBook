package com.example.myrecipebook.ui.recipes.favorite

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.model.Recipe
import kotlinx.coroutines.launch

data class FavoritesState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class FavoritesViewModel(
    private val repository: RecipeRepository
) : ViewModel() {
    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        _state.value = FavoritesState(isLoading = true)

        viewModelScope.launch {
            try {
                val favoriteRecipes = repository.getFavoriteRecipesFromCacheOnce()
                val sortedFavoriteRecipes = favoriteRecipes.sortedBy { it.title }
                _state.postValue(FavoritesState(recipes = sortedFavoriteRecipes, isLoading = false))
            } catch (e: Exception) {
                _state.postValue(FavoritesState(isLoading = false, isError = true))
            }
        }
    }
}