package com.example.myrecipebook.ui.recipes.recipe

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myrecipebook.ARG_RECIPE_ID
import com.example.myrecipebook.FAVORITES_KEY
import com.example.myrecipebook.PREFS_NAME
import com.example.myrecipebook.R
import com.example.myrecipebook.databinding.FragmentRecipeBinding
import com.example.myrecipebook.model.Recipe
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.launch
import java.io.IOException

@Suppress("DEPRECATION")
class RecipeFragment : Fragment() {
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private var _binding: FragmentRecipeBinding? = null
    private val binding
        get() =
            _binding ?: throw IllegalStateException(
                "Binding for FragmentRecipeBinding must not be null.",
            )

    private lateinit var recipe: Recipe

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = arguments?.getInt(ARG_RECIPE_ID, -1) ?: -1
        if (recipeId == -1) return

        ingredientsAdapter = IngredientsAdapter(emptyList())
        binding.rvIngredients.layoutManager = LinearLayoutManager(context)
        binding.rvIngredients.adapter = ingredientsAdapter

        val methodAdapter = MethodAdapter(emptyList())
        binding.rvMethod.layoutManager = LinearLayoutManager(context)
        binding.rvMethod.adapter = methodAdapter

        initUI()
        initPortionsSeekBar()
        initFavoriteButton()

        viewModel.loadRecipe(recipeId)
    }

    private fun initFavoriteButton() {
        binding.ibFavorite.setOnClickListener {
            viewModel.onFavoritesClicked()
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val iconRes =
            if (isFavorite) {
                R.drawable.ic_heart_filled
            } else {
                R.drawable.ic_heart_empty
            }
        binding.ibFavorite.setImageResource(iconRes)
    }

    private fun initPortionsSeekBar() {
        binding.sbPortions.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    val portions = progress
                    binding.tvPortionsCount.text = portions.toString()
                    ingredientsAdapter.updateIngredients(portions)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            },
        )
    }

    private fun initUI() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            state.recipe?.let { recipe ->
                this.recipe = recipe
                binding.tvRecipeName.text = recipe.title
                binding.ivRecipeHeader.setImageDrawable(state.recipeImage)
                try {
                    val inputStream = requireContext().assets.open(recipe.imageUrl)
                    val drawable = Drawable.createFromStream(inputStream, null)
                    binding.ivRecipeHeader.setImageDrawable(drawable)
                } catch (e: IOException) {
                    Log.e("RecipeFragment", "Error loading recipe image: ${recipe.imageUrl}", e)
                }

                ingredientsAdapter = IngredientsAdapter(recipe.ingredients)
                binding.rvIngredients.adapter = ingredientsAdapter

                val methodAdapter = MethodAdapter(recipe.method)
                binding.rvMethod.adapter = methodAdapter

                addDividers()
            }

            updateFavoriteIcon(state.isFavorite)
            binding.tvPortionsCount.text = state.portionCount.toString()
        }
    }

    private fun addDividers() {
        binding.rvIngredients.itemDecorationCount.let { count ->
            for (i in count - 1 downTo 0) {
                binding.rvIngredients.removeItemDecorationAt(i)
            }
        }

        binding.rvMethod.itemDecorationCount.let { count ->
            for (i in count - 1 downTo 0) {
                binding.rvMethod.removeItemDecorationAt(i)
            }
        }

        val context = requireContext()

        val ingredientsDivider =
            MaterialDividerItemDecoration(context, LinearLayoutManager.VERTICAL).apply {
                setDividerColorResource(context, R.color.ingredient_separator)
                dividerInsetStart = resources.getDimensionPixelSize(R.dimen.header_margin)
                dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.header_margin)
                isLastItemDecorated = false
            }
        binding.rvIngredients.addItemDecoration(ingredientsDivider)

        val methodDivider =
            MaterialDividerItemDecoration(context, LinearLayoutManager.VERTICAL).apply {
                setDividerColorResource(context, R.color.ingredient_separator)
                dividerInsetStart = resources.getDimensionPixelSize(R.dimen.header_margin)
                dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.header_margin)
                isLastItemDecorated = false
            }
        binding.rvMethod.addItemDecoration(methodDivider)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
