package com.example.myrecipebook.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.ui.recipes.recipe.RecipeViewModel

class RecipeViewModelFactory(
    private val repository: RecipeRepository
) : ViewModelProvider.Factory, Factory<RecipeViewModel> {

    override fun create(): RecipeViewModel {
        return RecipeViewModel(repository)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
        return create() as T
    }
}