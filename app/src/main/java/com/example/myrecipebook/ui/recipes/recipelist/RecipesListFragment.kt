package com.example.myrecipebook.ui.recipes.recipelist

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myrecipebook.ARG_CATEGORY_ID
import com.example.myrecipebook.ARG_CATEGORY_IMAGE_URL
import com.example.myrecipebook.ARG_CATEGORY_NAME
import com.example.myrecipebook.ARG_RECIPE_ID
import com.example.myrecipebook.R
import com.example.myrecipebook.data.STUB
import com.example.myrecipebook.databinding.FragmentRecipesListBinding
import com.example.myrecipebook.model.Recipe
import com.example.myrecipebook.ui.recipes.recipe.RecipeFragment
import java.io.IOException
import androidx.navigation.fragment.findNavController

class RecipesListFragment : Fragment() {
    private var _binding: FragmentRecipesListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipesListViewModel by viewModels()
    private lateinit var adapter: RecipesListAdapter

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

            val categoryId = bundle.getInt(ARG_CATEGORY_ID, -1).takeIf { it != -1 }
            val categoryName = bundle.getString(ARG_CATEGORY_NAME)
            val categoryImageUrl = bundle.getString(ARG_CATEGORY_IMAGE_URL)

            binding.tvCategoryTitle.text = categoryName ?: "Recipes List"

            viewModel.setCategory(categoryId, categoryName, categoryImageUrl)
        }

        initRecycler()
        observeState()
        observeHeaderImage()
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            state?.let {
                if (state.isLoading) {
                    // Показать загрузку
                } else {
                    // Шаг 5: Обновляем адаптер с рецептами
                    adapter.updateData(state.recipes)

                    binding.rvRecipes.visibility =
                        if (state.recipes.isEmpty()) View.GONE else View.VISIBLE

                    state.error?.let { error ->
                        Log.e("RecipesFragment", "Error loading recipes", error)
                    }
                }
            }
        }
    }

    private fun observeHeaderImage() {
        viewModel.headerImage.observe(viewLifecycleOwner) { drawable ->
            binding.ivCategoryHeader.setImageDrawable(drawable)
        }
    }

    private fun initRecycler() {
        adapter = RecipesListAdapter(emptyList())
        binding.rvRecipes.layoutManager = LinearLayoutManager(context)
        binding.rvRecipes.adapter = adapter

        adapter.setOnItemClickListener(
            object : RecipesListAdapter.OnItemClickListener {
                override fun onItemClick(recipeId: Int) {
                    openRecipeByRecipeId(recipeId)
                }
            }
        )
    }

    private fun openRecipeByRecipeId(recipeId: Int) {
        Log.d("RecipesFragment", "Opening recipe ID: $recipeId")

        val bundle = Bundle().apply {
            putInt(ARG_RECIPE_ID, recipeId)
        }

        findNavController().navigate(R.id.recipeFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}