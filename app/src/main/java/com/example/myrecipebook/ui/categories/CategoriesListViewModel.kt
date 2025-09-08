package com.example.myrecipebook.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.model.Category
import kotlinx.coroutines.launch

data class CategoriesListState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null
)

class CategoriesListViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableLiveData<CategoriesListState>()
    val state: LiveData<CategoriesListState> = _state

    private val _toastEvent = MutableLiveData<String?>()
    val toastEvent: LiveData<String?> = _toastEvent

    private val repository = RecipeRepository(application)

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

                    _state.postValue(
                        CategoriesListState(
                            categories = cachedCategories,
                            isLoading = true
                        )
                    )
                }

                val networkCategories = repository.getCategories()

                if (networkCategories.isNotEmpty()) {
                    _state.postValue(CategoriesListState(categories = networkCategories))
                } else if (cachedCategories.isNotEmpty()) {

                    _state.postValue(CategoriesListState(categories = cachedCategories))
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
                        _state.postValue(
                            CategoriesListState(
                                categories = cachedCategories,
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