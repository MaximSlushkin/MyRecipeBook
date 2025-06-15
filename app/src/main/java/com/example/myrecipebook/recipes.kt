package com.example.myrecipebook

object STUB {

    private val categories = listOf(
        Category(
            id = 1,
            name = "Салаты",
            url = "salad.png"
        ),

        Category(
            id = 2,
            name = "Супы",
            url = "soups.png"
        ),

        Category(
            id = 3,
            name = "Десерты",
            url = "desserts.png"
        ),

        Category(
            id = 4,
            name = "Бургеры",
            url = "burger.png"
        ),

        Category(
            id = 5,
            name = "Рыба",
            url = "fish.png"
        ),

        Category(
            id = 6,
            name = "Пицца",
            url = "pizza.png"
        )
    )

    fun getCategories(): List<Category> {
        return categories
    }
}