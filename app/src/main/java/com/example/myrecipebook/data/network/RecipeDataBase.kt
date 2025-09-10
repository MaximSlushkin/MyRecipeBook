package com.example.myrecipebook.data.network

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myrecipebook.model.Category

@Database(
    entities = [Category::class],
    version = 1,
    exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao
}