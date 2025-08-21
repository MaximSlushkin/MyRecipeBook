package com.example.myrecipebook.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.navigation.findNavController
import com.example.myrecipebook.R
import com.example.myrecipebook.databinding.ActivityMainBinding
import com.example.myrecipebook.ui.categories.CategoriesListFragment
import com.example.myrecipebook.ui.recipes.favorite.FavoritesFragment
import androidx.navigation.fragment.findNavController

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBarsInsets.top,
                bottom = systemBarsInsets.bottom
            )
            insets
        }

        binding.btnRecipes.setOnClickListener {
            findNavController(R.id.mainContainer).navigate(R.id.categoriesListFragment)
        }

        binding.btnFavorites.setOnClickListener {
            findNavController(R.id.mainContainer).navigate(R.id.favoritesFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}