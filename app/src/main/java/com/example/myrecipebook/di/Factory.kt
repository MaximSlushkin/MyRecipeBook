package com.example.myrecipebook.di

import androidx.lifecycle.ViewModel

interface Factory<T : ViewModel> {
    fun create(): T
}