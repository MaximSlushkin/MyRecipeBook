package com.example.myrecipebook.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.myrecipebook.data.network.IngredientListConverter
import com.example.myrecipebook.data.network.MethodListConverter
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = "recipes")
@Parcelize
@Serializable
data class Recipe(
    @PrimaryKey
    val id: Int,
    val title: String,
    @field:TypeConverters(IngredientListConverter::class)
    val ingredients: List<Ingredient>,
    @field:TypeConverters(MethodListConverter::class)
    val method: List<String>,
    val imageUrl: String,
    val categoryId: Int? = null,
    @ColumnInfo(name = "isFavorite")
    val isFavorite: Boolean,
) : Parcelable