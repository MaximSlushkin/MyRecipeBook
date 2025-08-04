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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myrecipebook.ARG_RECIPE
import com.example.myrecipebook.FAVORITES_KEY
import com.example.myrecipebook.PREFS_NAME
import com.example.myrecipebook.R
import com.example.myrecipebook.databinding.FragmentRecipeBinding
import com.example.myrecipebook.model.Recipe
import com.google.android.material.divider.MaterialDividerItemDecoration
import java.io.IOException

@Suppress("DEPRECATION")
class RecipeFragment : Fragment() {

    private lateinit var ingredientsAdapter: IngredientsAdapter
    private var _binding: FragmentRecipeBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException(
            "Binding for FragmentRecipeBinding must not be null."
        )

    private lateinit var recipe: Recipe

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var isFavorite: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipe = getRecipeFromArguments() ?: return

        initUI()
        initRecyclers()
        initPortionsSeekBar()
        initFavoriteButton()
    }

    private fun saveFavorites(favorites: Set<String>) {
        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putStringSet(FAVORITES_KEY, favorites)
            apply()
        }
    }

    private fun getFavorites(): MutableSet<String> {
        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val favorites = sharedPref.getStringSet(FAVORITES_KEY, null) ?: mutableSetOf()
        return HashSet(favorites)
    }

    private fun initFavoriteButton() {
        val favorites = getFavorites()
        isFavorite = favorites.contains(recipe.id.toString())

        binding.ibFavorite.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteIcon()

            val favoritesSet = getFavorites().apply {
                if (isFavorite) add(recipe.id.toString())
                else remove(recipe.id.toString())
            }
            saveFavorites(favoritesSet)
        }
        updateFavoriteIcon()
    }

    private fun updateFavoriteIcon() {
        val iconRes = if (isFavorite) {
            R.drawable.ic_heart_filled
        } else {
            R.drawable.ic_heart_empty
        }
        binding.ibFavorite.setImageResource(iconRes)
    }

    private fun initPortionsSeekBar() {
        binding.sbPortions.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                val portions = progress
                binding.tvPortionsCount.text = portions.toString()
                ingredientsAdapter.updateIngredients(portions)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun initUI() {
        binding.tvRecipeName.text = recipe.title

        try {
            val inputStream = requireContext().assets.open(recipe.imageUrl)
            val drawable = Drawable.createFromStream(inputStream, null)
            binding.ivRecipeHeader.setImageDrawable(drawable)
        } catch (e: IOException) {
            Log.e("RecipeFragment", "Error loading recipe image: ${recipe.imageUrl}", e)
        }
    }

    private fun initRecyclers() {

        ingredientsAdapter = IngredientsAdapter(recipe.ingredients)

        binding.rvIngredients.layoutManager = LinearLayoutManager(context)

        binding.rvIngredients.adapter = ingredientsAdapter

        binding.rvMethod.layoutManager = LinearLayoutManager(context)
        val methodAdapter = MethodAdapter(recipe.method)
        binding.rvMethod.adapter = methodAdapter

        addDividers()
    }

    private fun addDividers() {
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

    private fun getRecipeFromArguments(): Recipe? {
        return arguments?.getParcelable(ARG_RECIPE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}