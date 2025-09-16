package com.example.myrecipebook.data.network

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myrecipebook.model.Category
import com.example.myrecipebook.model.Recipe

@Database(
    entities = [Category::class, Recipe::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(IngredientListConverter::class, MethodListConverter::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao
    abstract fun recipesDao(): RecipesDao
}