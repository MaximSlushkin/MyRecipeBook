package com.example.myrecipebook.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.myrecipebook.R
import com.example.myrecipebook.databinding.ActivityMainBinding
import com.example.myrecipebook.ui.categories.CategoriesListFragment
import com.example.myrecipebook.ui.recipes.favorite.FavoritesFragment

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
        if (savedInstanceState == null) {
            navigateToCategories()
        }

        binding.btnRecipes.setOnClickListener {
            navigateToCategories()
        }

        binding.btnFavorites.setOnClickListener {
            navigateToFavorites()
        }
    }

    private fun navigateToCategories() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<CategoriesListFragment>(R.id.mainContainer)
        }
    }

    private fun navigateToFavorites() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<FavoritesFragment>(R.id.mainContainer)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}