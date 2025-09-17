package com.example.myrecipebook.di

interface Factory<T> {
    fun create(): T
}