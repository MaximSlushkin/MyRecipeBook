package com.example.myrecipebook

import android.content.Context
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
import com.example.myrecipebook.databinding.FragmentFavoritesBinding
import java.io.IOException

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentFavoritesBinding must not be null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvFragmentTitle.text = getString(R.string.title_favorites)
        initRecycler()
        loadHeaderImage()
    }

    private fun loadHeaderImage() {
        try {
            val inputStream = requireContext().assets.open("bcg_favorites.png")
            val drawable = Drawable.createFromStream(inputStream, null)
            binding.ivFavorite.setImageDrawable(drawable)
        } catch (e: IOException) {
            Log.e("FavoritesFragment", "Error loading header image", e)

        }
    }

    private fun initRecycler() {

        val favoritesIds = getFavorites().map { it.toInt() }.toSet()

        val favoriteRecipes = STUB.getRecipesByIds(favoritesIds)

        if (favoriteRecipes.isEmpty()) {
            binding.rvRecipes.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvRecipes.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE

            val adapter = RecipesListAdapter(favoriteRecipes)
            binding.rvRecipes.layoutManager = LinearLayoutManager(context)
            binding.rvRecipes.adapter = adapter

            adapter.setOnItemClickListener(object : RecipesListAdapter.OnItemClickListener {
                override fun onItemClick(recipeId: Int) {
                    openRecipeByRecipeId(recipeId)
                }
            })
        }
    }

    private fun getFavorites(): Set<String> {
        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getStringSet(FAVORITES_KEY, null) ?: setOf()
    }

    private fun openRecipeByRecipeId(recipeId: Int) {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}