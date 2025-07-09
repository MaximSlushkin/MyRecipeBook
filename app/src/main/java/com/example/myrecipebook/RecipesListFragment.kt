package com.example.myrecipebook

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myrecipebook.CategoriesListFragment.Companion.ARG_CATEGORY_ID
import com.example.myrecipebook.CategoriesListFragment.Companion.ARG_CATEGORY_IMAGE_URL
import com.example.myrecipebook.CategoriesListFragment.Companion.ARG_CATEGORY_NAME
import com.example.myrecipebook.databinding.FragmentRecipesListBinding

class RecipesListFragment : Fragment() {

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
        }

        binding.tvFragmentTitle.text ?: "Recipes List"

        Log.d("RecipesFragment", "Category ID: $categoryId, Name: $categoryName")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}