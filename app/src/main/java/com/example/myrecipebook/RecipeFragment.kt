package com.example.myrecipebook

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myrecipebook.databinding.FragmentRecipeBinding
import com.google.android.material.divider.MaterialDividerItemDecoration
import java.io.IOException

@Suppress("DEPRECATION")
class RecipeFragment : Fragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipe = getRecipeFromArguments() ?: return

        initUI()
        initRecyclers()
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

        binding.rvIngredients.layoutManager = LinearLayoutManager(context)
        val ingredientsAdapter = IngredientsAdapter(recipe.ingredients)
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