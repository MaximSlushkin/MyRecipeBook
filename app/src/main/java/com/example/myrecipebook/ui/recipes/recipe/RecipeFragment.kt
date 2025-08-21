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
import androidx.navigation.fragment.navArgs
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
    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var methodAdapter: MethodAdapter
    private val viewModel: RecipeViewModel by viewModels()

    private val args: RecipeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = args.recipeId

        initAdapters()
        initViews()
        setupObservers()

        viewModel.loadRecipe(recipeId)
    }

    private fun initAdapters() {
        ingredientsAdapter = IngredientsAdapter(emptyList(), 1)
        binding.rvIngredients.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ingredientsAdapter
            addItemDecoration(createIngredientsDivider())
        }

        methodAdapter = MethodAdapter(emptyList())
        binding.rvMethod.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = methodAdapter
            addItemDecoration(createMethodDivider())
        }
    }

    private fun initViews() {
        binding.sbPortions.setOnSeekBarChangeListener(PortionSeekBarListener { progress ->
            viewModel.updatePortionCount(progress)
            updatePortionCountUI(progress)
        })

        binding.ibFavorite.setOnClickListener {
            viewModel.onFavoritesClicked()
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            state.recipe?.let { recipe ->
                updateRecipeUI(recipe, state)
            }
            updateFavoriteIcon(state.isFavorite)
            updatePortionCountUI(state.portionCount)
        }
    }

    private fun updateRecipeUI(recipe: Recipe, state: RecipeViewModel.RecipeState) {
        binding.tvRecipeName.text = recipe.title
        binding.ivRecipeHeader.setImageDrawable(state.recipeImage)
        ingredientsAdapter.updateData(recipe.ingredients, state.portionCount)
        methodAdapter.updateSteps(recipe.method)
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        binding.ibFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_heart_filled
            else R.drawable.ic_heart_empty
        )
    }

    private fun updatePortionCountUI(portionCount: Int) {
        binding.tvPortionsCount.text = portionCount.toString()
    }

    private fun createIngredientsDivider(): MaterialDividerItemDecoration {
        return MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).apply {
            setDividerColorResource(requireContext(), R.color.ingredient_separator)
            dividerInsetStart = resources.getDimensionPixelSize(R.dimen.header_margin)
            dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.header_margin)
            isLastItemDecorated = false
        }
    }

    private fun createMethodDivider(): MaterialDividerItemDecoration {
        return MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).apply {
            setDividerColorResource(requireContext(), R.color.ingredient_separator)
            dividerInsetStart = resources.getDimensionPixelSize(R.dimen.header_margin)
            dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.header_margin)
            isLastItemDecorated = false
        }
    }

    private inner class PortionSeekBarListener(
        private val onProgressChanged: (Int) -> Unit
    ) : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (fromUser) onProgressChanged(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}