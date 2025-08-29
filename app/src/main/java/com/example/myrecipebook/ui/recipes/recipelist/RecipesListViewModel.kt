package com.example.myrecipebook.ui.recipes.recipelist

import android.app.Application
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myrecipebook.data.repository.RecipeRepository
import com.example.myrecipebook.model.Recipe
import kotlinx.coroutines.launch
import java.io.IOException

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

    private val _headerImage = MutableLiveData<Drawable?>()
    val headerImage: LiveData<Drawable?> get() = _headerImage

    private var categoryId: Int? = null
    private var categoryImageUrl: String? = null

    private val repository = RecipeRepository()

    fun setCategory(id: Int?, name: String?, imageUrl: String?) {
        categoryId = id
        categoryImageUrl = imageUrl

        val currentState = _state.value ?: RecipesListState()
        _state.value = currentState.copy(categoryName = name ?: "")

        loadHeaderImage()
        loadRecipes()
    }

    private fun loadRecipes() {
        val currentCategoryName = _state.value?.categoryName ?: ""
        _state.value = RecipesListState(
            categoryName = currentCategoryName,
            isLoading = true
        )

        val categoryId = this.categoryId ?: -1
        repository.getRecipesByCategory(categoryId) { recipes ->
            viewModelScope.launch {
                if (recipes != null) {
                    _state.postValue(
                        RecipesListState(
                            recipes = recipes,
                            categoryName = currentCategoryName,
                            isLoading = false,
                        )
                    )
                } else {

                    Toast.makeText(
                        getApplication(),
                        "Ошибка получения данных",
                        Toast.LENGTH_SHORT
                    ).show()
                    _state.postValue(
                        RecipesListState(
                            categoryName = currentCategoryName,
                            error = Throwable("Failed to load recipes"),
                            isLoading = false
                        )
                    )
                }
            }
        }
    }

    private fun loadHeaderImage() {
        viewModelScope.launch {
            try {
                categoryImageUrl?.let { url ->
                    val inputStream = getApplication<Application>().assets.open(url)
                    val drawable = Drawable.createFromStream(inputStream, null)
                    _headerImage.postValue(drawable)
                } ?: run {
                    _headerImage.postValue(null)
                }
            } catch (e: IOException) {
                Log.e("RecipesListViewModel", "Error loading header image: $categoryImageUrl", e)
                _headerImage.postValue(null)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.shutdown()
    }
}