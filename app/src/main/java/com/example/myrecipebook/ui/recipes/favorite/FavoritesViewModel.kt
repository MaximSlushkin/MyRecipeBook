package com.example.myrecipebook.ui.recipes.favorite

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myrecipebook.FAVORITES_KEY
import com.example.myrecipebook.PREFS_NAME
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.model.Recipe
import kotlinx.coroutines.launch
import java.io.IOException

data class FavoritesState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: Throwable? = null,
)

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    private val _headerImage = MutableLiveData<Drawable?>()
    val headerImage: LiveData<Drawable?> get() = _headerImage

    private val sharedPref = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val repository = RecipeRepository()

    init {
        loadFavorites()
        loadHeaderImage()
    }

    private fun loadFavorites() {
        _state.value = FavoritesState(isLoading = true)

        val favorites = getFavorites()
        val favoriteIds = favorites.map { it.toInt() }.toSet()

        if (favoriteIds.isEmpty()) {
            _state.postValue(FavoritesState(recipes = emptyList(), isLoading = false))
            return
        }

        repository.getRecipesByIds(favoriteIds) { recipes ->
            viewModelScope.launch {
                if (recipes != null) {
                    _state.postValue(
                        FavoritesState(
                            recipes = recipes,
                            isLoading = false,
                        )
                    )
                } else {

                    Toast.makeText(
                        getApplication(),
                        "Ошибка получения данных",
                        Toast.LENGTH_SHORT
                    ).show()
                    _state.postValue(
                        FavoritesState(
                            error = Throwable("Failed to load favorites"),
                            isLoading = false
                        )
                    )
                }
            }
        }
    }

    private fun loadHeaderImage() {
        viewModelScope.launch {
            try {
                val inputStream = getApplication<Application>().assets.open("bcg_favorites.png")
                val drawable = Drawable.createFromStream(inputStream, null)
                _headerImage.postValue(drawable)
            } catch (e: IOException) {
                Log.e("FavoritesViewModel", "Error loading header image", e)
                _headerImage.postValue(null)
            }
        }
    }

    private fun getFavorites(): Set<String> {
        return sharedPref.getStringSet(FAVORITES_KEY, null) ?: setOf()
    }

    override fun onCleared() {
        super.onCleared()
        repository.shutdown()
    }
}