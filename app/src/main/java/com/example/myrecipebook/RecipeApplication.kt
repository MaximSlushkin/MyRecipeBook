package com.example.myrecipebook

import android.app.Application
import com.example.myrecipebook.di.AppContainer

class RecipeApplication : Application() {
    val appContainer by lazy { AppContainer(this) }
}