package com.example.myrecipebook

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myrecipebook.CategoriesListFragment.Companion.ARG_CATEGORY_ID
import com.example.myrecipebook.CategoriesListFragment.Companion.ARG_CATEGORY_IMAGE_URL
import com.example.myrecipebook.CategoriesListFragment.Companion.ARG_CATEGORY_NAME
import com.example.myrecipebook.databinding.FragmentRecipesListBinding
import java.io.IOException

class RecipesListFragment : Fragment() {

    companion object {
        const val ARG_RECIPE = "recipe"
    }

    private var categoryId: Int? = null
    private var categoryName: String? = null
    private var categoryImageUrl: String? = null
    private var _binding: FragmentRecipesListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            categoryId = bundle.getInt(ARG_CATEGORY_ID, -1).takeIf { it != -1 }
            categoryName = bundle.getString(ARG_CATEGORY_NAME)
            categoryImageUrl = bundle.getString(ARG_CATEGORY_IMAGE_URL)

            binding.tvCategoryTitle.text = categoryName ?: "Recipes List"

            setCategoryHeaderImage(categoryImageUrl)

            val recipes = STUB.getRecipesByCategoryId(categoryId ?: -1)
            initRecycler(recipes)
        }

        Log.d("RecipesFragment", "Category ID: $categoryId, Name: $categoryName")
    }

    private fun setCategoryHeaderImage(imageUrl: String?) {
        try {
            imageUrl?.let {
                val inputStream = requireContext().assets.open(it)
                val drawable = Drawable.createFromStream(inputStream, null)
                binding.ivCategoryHeader.setImageDrawable(drawable)
            }
        } catch (e: IOException) {
            Log.e("RecipesFragment", "Error loading header image: $imageUrl", e)
        }
    }

    private fun initRecycler(recipes: List<Recipe>) {
        val adapter = RecipesListAdapter(recipes)
        binding.rvRecipes.layoutManager = LinearLayoutManager(context)
        binding.rvRecipes.adapter = adapter

        adapter.setOnItemClickListener(object : RecipesListAdapter.OnItemClickListener {
            override fun onItemClick(recipeId: Int) {
                openRecipeByRecipeId(recipeId)
            }
        })
    }

    private fun openRecipeByRecipeId(recipeId: Int) {
        Log.d("RecipesFragment", "Opening recipe ID: $recipeId")

        STUB.getRecipeById(recipeId)?.let { recipe ->
            val bundle = Bundle().apply {
                putParcelable(ARG_RECIPE, recipe)
            }

            parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace<RecipeFragment>(R.id.mainContainer, args = bundle)
                addToBackStack(null)
            }
        }

        fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
}