package com.example.myrecipebook.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.ui.recipes.favorite.FavoritesViewModel

class FavoritesViewModelFactory(
    private val repository: RecipeRepository
) : ViewModelProvider.Factory, Factory<FavoritesViewModel> {

    override fun create(): FavoritesViewModel {
        return FavoritesViewModel(repository)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return create() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}