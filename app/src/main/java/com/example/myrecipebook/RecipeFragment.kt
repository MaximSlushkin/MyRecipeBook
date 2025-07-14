package com.example.myrecipebook

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class RecipeFragment : Fragment() {

    companion object {
        const val ARG_RECIPE = "recipe"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TextView(requireContext()).apply {
            text = "Наш рецепт"

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recipe = getRecipeFromArguments()
        (view as TextView).text = recipe?.title ?: "Рецепт не найден"
    }
    private fun getRecipeFromArguments(): Recipe? {
        return arguments?.let { bundle ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(ARG_RECIPE, Recipe::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable(ARG_RECIPE)
            }
        }
    }
}