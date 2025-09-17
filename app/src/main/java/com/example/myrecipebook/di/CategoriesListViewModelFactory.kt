package com.example.myrecipebook.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.ui.categories.CategoriesListViewModel

class CategoriesListViewModelFactory(
    private val repository: RecipeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesListViewModel::class.java)) {
            return CategoriesListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}