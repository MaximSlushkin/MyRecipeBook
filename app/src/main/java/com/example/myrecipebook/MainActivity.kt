package com.example.myrecipebook

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import com.example.myrecipebook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

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
                replace(R.id.mainContainer, CategoriesListFragment())
            }
            updateButtonColors(true)
        }

        private fun navigateToFavorites() {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.mainContainer, FavoritesFragment())
            }
            updateButtonColors(false)
        }

        private fun updateButtonColors(isCategoriesActive: Boolean) {
            val activeColor = ContextCompat.getColor(this, R.color.button_active_color)
            val inactiveColor = ContextCompat.getColor(this, R.color.button_inactive_color)

            binding.btnRecipes.backgroundTintList = ColorStateList.valueOf(
                if (isCategoriesActive) activeColor else inactiveColor
            )
            binding.btnFavorites.backgroundTintList = ColorStateList.valueOf(
                if (isCategoriesActive) inactiveColor else activeColor
            )
        }

        override fun onDestroy() {
            super.onDestroy()
            _binding = null
        }
    }
}