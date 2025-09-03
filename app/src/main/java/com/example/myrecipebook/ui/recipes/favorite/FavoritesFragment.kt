package com.example.myrecipebook.ui.recipes.favorite

import android.content.Context
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
import com.example.myrecipebook.ARG_RECIPE_ID
import com.example.myrecipebook.FAVORITES_KEY
import com.example.myrecipebook.PREFS_NAME
import com.example.myrecipebook.R
import com.example.myrecipebook.databinding.FragmentFavoritesBinding
import com.example.myrecipebook.ui.recipes.recipe.RecipeFragment
import com.example.myrecipebook.ui.recipes.recipelist.RecipesListAdapter
import java.io.IOException
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException(
            "Binding for FragmentFavoritesBinding must not be null."
        )

    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var adapter: RecipesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvFragmentTitle.text = getString(R.string.title_favorites)

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

                    if (state.recipes.isEmpty()) {
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.rvRecipes.visibility = View.GONE
                    } else {
                        binding.tvEmptyState.visibility = View.GONE
                        binding.rvRecipes.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun observeHeaderImage() {

        Glide.with(requireContext())
            .load("file:///android_asset/bcg_favorites.png")
            .into(binding.ivFavorite)
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
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToRecipeFragment(recipeId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}