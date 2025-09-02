package com.example.myrecipebook.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.model.Category

data class CategoriesListState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class CategoriesListViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableLiveData<CategoriesListState>()
    val state: LiveData<CategoriesListState> = _state

    private val repository = RecipeRepository()

    fun getCategoryById(categoryId: Int): Category? {
        val currentCategories = _state.value?.categories ?: emptyList()
        return currentCategories.find { it.id == categoryId }
    }

    init {
        loadCategories()
    }

    private fun loadCategories() {
        _state.value = CategoriesListState(isLoading = true)

        repository.getCategories { categories ->
            if (categories != null) {
                _state.postValue(CategoriesListState(categories = categories))
            } else {
                _state.postValue(CategoriesListState(isError = true))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.shutdown()
    }
}