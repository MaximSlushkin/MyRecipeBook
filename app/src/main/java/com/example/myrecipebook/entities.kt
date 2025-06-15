package com.example.myrecipebook

data class Category(
    val id: Int,
    val name: String,
    val url: String
)

data class Ingredient(
    val name: String,
    val quantity: String
)

data class Recipe(
    val id: Int,
    val categoryId: Int,
    val name: String,
    val description: String,
    val image: String,
    val instructions: String,
    val ingredients: List<Ingredient>
)