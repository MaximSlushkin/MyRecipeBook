package com.example.myrecipebook.ui.recipes.recipe

import androidx.lifecycle.ViewModel
import com.example.myrecipebook.model.Recipe

class RecipeViewModel : ViewModel() {

    data class RecipeState(
        val recipe: Recipe? = null,
        val portionCount: Int = 1,
        val isFavorite: Boolean = false,
        val isLoading: Boolean = false,
        val error: Throwable? = null
    )

    var state: RecipeState = RecipeState()
        private set
}