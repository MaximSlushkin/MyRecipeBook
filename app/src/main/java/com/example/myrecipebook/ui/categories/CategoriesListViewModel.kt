package com.example.myrecipebook.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.model.Category
import kotlinx.coroutines.launch

data class CategoriesListState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
)

class CategoriesListViewModel(
    private val repository: RecipeRepository
) : ViewModel() {
    private val _state = MutableLiveData<CategoriesListState>()
    val state: LiveData<CategoriesListState> = _state

    private val _toastEvent = MutableLiveData<String?>()
    val toastEvent: LiveData<String?> = _toastEvent

    fun getCategoryById(categoryId: Int): Category? {
        val currentCategories = _state.value?.categories ?: emptyList()
        return currentCategories.find { it.id == categoryId }
    }

    init {
        loadCategoriesWithCache()
    }

    private fun loadCategoriesWithCache() {
        _state.value = CategoriesListState(isLoading = true)

        viewModelScope.launch {
            try {

                val cachedCategories = repository.getCategoriesFromCacheOnce()
                if (cachedCategories.isNotEmpty()) {

                    val sortedCachedCategories = cachedCategories.sortedBy { it.title }
                    _state.postValue(
                        CategoriesListState(
                            categories = sortedCachedCategories,
                            isLoading = true
                        )
                    )
                }

                val networkCategories = repository.getCategories()

                if (networkCategories.isNotEmpty()) {

                    val sortedNetworkCategories = networkCategories.sortedBy { it.title }
                    _state.postValue(CategoriesListState(categories = sortedNetworkCategories))
                } else if (cachedCategories.isNotEmpty()) {

                    val sortedCachedCategories = cachedCategories.sortedBy { it.title }
                    _state.postValue(CategoriesListState(categories = sortedCachedCategories))
                } else {
                    _state.postValue(
                        CategoriesListState(
                            isError = true,
                            errorMessage = "Нет данных"
                        )
                    )
                }

            } catch (e: Exception) {
                try {
                    val cachedCategories = repository.getCategoriesFromCacheOnce()
                    if (cachedCategories.isNotEmpty()) {

                        val sortedCachedCategories = cachedCategories.sortedBy { it.title }
                        _state.postValue(
                            CategoriesListState(
                                categories = sortedCachedCategories,
                                isError = true,
                                errorMessage = "Офлайн режим: данные могут быть устаревшими"
                            )
                        )
                        _toastEvent.postValue("Офлайн режим: данные могут быть устаревшими")
                    } else {
                        _state.postValue(
                            CategoriesListState(
                                isError = true,
                                errorMessage = "Ошибка получения данных"
                            )
                        )
                        _toastEvent.postValue("Ошибка получения данных")
                    }
                } catch (cacheException: Exception) {
                    _state.postValue(
                        CategoriesListState(
                            isError = true,
                            errorMessage = "Ошибка получения данных"
                        )
                    )
                    _toastEvent.postValue("Ошибка получения данных")
                }
            }
        }
    }

    fun clearToastEvent() {
        _toastEvent.value = null
    }
}