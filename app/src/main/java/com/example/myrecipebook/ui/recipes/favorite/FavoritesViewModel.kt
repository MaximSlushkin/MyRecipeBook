package com.example.myrecipebook.ui.recipes.favorite

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myrecipebook.FAVORITES_KEY
import com.example.myrecipebook.PREFS_NAME
import com.example.myrecipebook.data.STUB
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

    init {
        loadFavorites()
        loadHeaderImage()
    }

    private fun loadFavorites() {
        _state.value = FavoritesState(isLoading = true)
        viewModelScope.launch {
            try {
                val favorites = getFavorites()
                val recipes = STUB.getRecipesByIds(favorites.map { it.toInt() }.toSet())

                _state.postValue(
                    FavoritesState(
                        recipes = recipes,
                        isLoading = false,
                    )
                )
            } catch (e: Exception) {
                _state.postValue(
                    FavoritesState(
                        error = e,
                        isLoading = false
                    )
                )
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
}