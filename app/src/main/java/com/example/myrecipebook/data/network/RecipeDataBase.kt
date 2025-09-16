package com.example.myrecipebook.data.network

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myrecipebook.model.Category
import com.example.myrecipebook.model.Recipe

@Database(
    entities = [Category::class, Recipe::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(IngredientListConverter::class, MethodListConverter::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao
    abstract fun recipesDao(): RecipesDao

    companion object {

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE recipes ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}