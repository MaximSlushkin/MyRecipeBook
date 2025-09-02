package com.example.myrecipebook.ui.recipes.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myrecipebook.R
import com.example.myrecipebook.databinding.FragmentRecipeBinding
import com.example.myrecipebook.model.Recipe
import com.google.android.material.divider.MaterialDividerItemDecoration

@Suppress("DEPRECATION")
class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException(
            "Binding for FragmentRecipeBinding must not be null."
        )

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
            state?.let {
                if (state.isError) {
                    Toast.makeText(requireContext(), "Ошибка получения данных", Toast.LENGTH_SHORT).show()
                }

                state.recipe?.let { recipe ->
                    updateRecipeUI(recipe, state)
                }
                updateFavoriteIcon(state.isFavorite)
                updatePortionCountUI(state.portionCount)
            }
        }
    }

    private fun updateRecipeUI(recipe: Recipe, state: RecipeViewModel.RecipeState) {
        binding.tvRecipeName.text = recipe.title

        state.recipeImageUrl?.let { imageUrl ->
            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .override(800, 600)
                .into(binding.ivRecipeHeader)
        }

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