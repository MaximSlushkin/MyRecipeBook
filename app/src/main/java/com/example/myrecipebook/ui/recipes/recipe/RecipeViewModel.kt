package com.example.myrecipebook.ui.recipes.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myrecipebook.model.Recipe

class RecipeViewModel : ViewModel() {
    data class RecipeState(
        val recipe: Recipe? = null,
        val portionCount: Int = 1,
        val isFavorite: Boolean = false,
        val isLoading: Boolean = false,
        val error: Throwable? = null,
    )

    private val _state = MutableLiveData<RecipeState>()

    val state: LiveData<RecipeState> = _state

    init {
        Log.i("RecipeViewModel", "ViewModel initialized")
        _state.value = RecipeState(isFavorite = true)
    }

    fun updateState(newState: RecipeState) {
        _state.value = newState
    }
}
