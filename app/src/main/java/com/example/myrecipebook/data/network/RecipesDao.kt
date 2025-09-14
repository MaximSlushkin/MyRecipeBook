package com.example.myrecipebook.data.network

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myrecipebook.model.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {

    @Query("SELECT * FROM recipes WHERE categoryId = :categoryId ORDER BY title ASC")
    fun getRecipesByCategory(categoryId: Int): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE categoryId = :categoryId ORDER BY title ASC")
    suspend fun getRecipesByCategoryOnce(categoryId: Int): List<Recipe>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: Int): Recipe?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<Recipe>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    @Query("DELETE FROM recipes WHERE categoryId = :categoryId")
    suspend fun deleteRecipesByCategory(categoryId: Int)

    @Query("SELECT COUNT(*) FROM recipes WHERE categoryId = :categoryId")
    suspend fun getRecipesCountByCategory(categoryId: Int): Int
}