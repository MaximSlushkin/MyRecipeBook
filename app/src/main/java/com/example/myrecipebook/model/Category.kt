package com.example.myrecipebook.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Category(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
) : Parcelable