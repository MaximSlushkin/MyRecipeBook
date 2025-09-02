package com.example.myrecipebook.ui.recipes.recipelist

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.example.myrecipebook.databinding.FragmentRecipesListBinding
import com.example.myrecipebook.model.Recipe
import com.example.myrecipebook.ui.recipes.recipe.RecipeFragment
import java.io.IOException
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide

class RecipesListFragment : Fragment() {

    private var _binding: FragmentRecipesListBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException(
            "Binding for FragmentRecipesListBinding must not be null."
        )

    private val viewModel: RecipesListViewModel by viewModels()
    private lateinit var adapter: RecipesListAdapter

    private val args: RecipesListFragmentArgs by navArgs()

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

        val category = args.category

        binding.tvCategoryTitle.text = category.title

        viewModel.setCategory(category.id, category.title, category.imageUrl)

        initRecycler()
        observeState()
        observeHeaderImage()
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            state?.let {

                    if (state.isError) {
                        Toast.makeText(requireContext(), "Ошибка получения данных", Toast.LENGTH_SHORT).show()
                    } else {

                        adapter.updateData(state.recipes)
                    }
                }
            }
        }

    private fun observeHeaderImage() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            state?.let {
                binding.tvCategoryTitle.text = state.categoryName

                state.categoryImageUrl?.let { imageUrl ->
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error)
                        .override(800, 400)
                        .centerCrop()
                        .into(binding.ivCategoryHeader)
                } ?: run {

                    binding.ivCategoryHeader.setImageResource(R.drawable.ic_error)
                }
            }
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

        val action = RecipesListFragmentDirections.actionRecipesListFragmentToRecipeFragment(recipeId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}