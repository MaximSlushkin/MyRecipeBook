package com.example.myrecipebook.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myrecipebook.data.STUB
import com.example.myrecipebook.model.Category
import kotlinx.coroutines.launch

data class CategoriesListState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: Throwable? = null
)

class CategoriesListViewModel : ViewModel() {
    private val _state = MutableLiveData<CategoriesListState>()
    val state: LiveData<CategoriesListState> = _state

    init {
        loadCategories()
    }

    private fun loadCategories() {
        _state.value = CategoriesListState(isLoading = true)
        viewModelScope.launch {
            try {
                val categories = STUB.getCategories()
                _state.postValue(CategoriesListState(categories = categories))
            } catch (e: Exception) {
                _state.postValue(CategoriesListState(error = e))
            }
        }
    }
}