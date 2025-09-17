package com.example.myrecipebook.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.ui.recipes.recipelist.RecipesListViewModel

class RecipesListViewModelFactory(
    private val repository: RecipeRepository
) : ViewModelProvider.Factory, Factory<RecipesListViewModel> {

    override fun create(): RecipesListViewModel {
        return RecipesListViewModel(repository)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipesListViewModel::class.java)) {
            return create() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}