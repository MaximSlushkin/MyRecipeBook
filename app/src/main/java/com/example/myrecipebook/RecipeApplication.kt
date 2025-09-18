package com.example.myrecipebook

import android.app.Application
import com.example.myrecipebook.di.AppContainer

class RecipeApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}