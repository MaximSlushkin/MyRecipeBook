package com.example.myrecipebook.data.network

import androidx.room.TypeConverter
import com.example.myrecipebook.model.Ingredient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class IngredientListConverter {
    @TypeConverter
    fun fromString(value: String): List<Ingredient> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toString(list: List<Ingredient>): String {
        return Json.encodeToString(list)
    }
}

class MethodListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toString(list: List<String>): String {
        return Json.encodeToString(list)
    }
}