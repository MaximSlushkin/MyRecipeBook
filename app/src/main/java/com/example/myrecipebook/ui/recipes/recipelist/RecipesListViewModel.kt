package com.example.myrecipebook.ui.recipes.recipelist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myrecipebook.data.STUB
import com.example.myrecipebook.model.Recipe
import kotlinx.coroutines.launch

data class RecipesListState(
    val recipes: List<Recipe> = emptyList(),
    val categoryName: String = "",
    val categoryImageUrl: String? = null,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
)

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableLiveData<RecipesListState>()
    val state: LiveData<RecipesListState> = _state

    private var categoryId: Int? = null

    fun setCategoryId(id: Int?) {
        categoryId = id
        loadRecipes()
    }

    private fun loadRecipes() {
        _state.value = RecipesListState(isLoading = true)
        viewModelScope.launch {
            try {
                val recipes = STUB.getRecipesByCategoryId(categoryId ?: -1)
                _state.postValue(
                    RecipesListState(
                        recipes = recipes,
                        isLoading = false,
                    )
                )
            } catch (e: Exception) {
                _state.postValue(
                    RecipesListState(
                        error = e,
                        isLoading = false
                    )
                )
            }
        }
    }
}