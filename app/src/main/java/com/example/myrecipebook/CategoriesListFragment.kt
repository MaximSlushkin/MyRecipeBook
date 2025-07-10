package com.example.myrecipebook

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.myrecipebook.databinding.FragmentListCategoriesBinding

class CategoriesListFragment : Fragment() {

    companion object {
        const val ARG_CATEGORY_ID = "category_id"
        const val ARG_CATEGORY_NAME = "category_name"
        const val ARG_CATEGORY_IMAGE_URL = "category_image_url"
    }

    private var _binding: FragmentListCategoriesBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException(
            "Binding for FragmentListCategoriesBinding must not be null."
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecycler() {
        val categoriesAdapter = CategoriesListAdapter(BackendSingleton.getCategories())
        binding.rvCategories.adapter = categoriesAdapter

        categoriesAdapter.setOnItemClickListener(object :
            CategoriesListAdapter.OnItemClickListener {
            override fun onItemClick(categoryId: Int) {
                openRecipesByCategoryId(categoryId)
            }
        })
    }

    private fun openRecipesByCategoryId(categoryId: Int) {
        val category = BackendSingleton.getCategories().find { it.id == categoryId }
        if (category != null) {

            val bundle = Bundle().apply {
                putInt(ARG_CATEGORY_ID, categoryId)
                putString(ARG_CATEGORY_NAME, category.title)
                putString(ARG_CATEGORY_IMAGE_URL, category.imageUrl)
            }

            parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace<RecipesListFragment>(R.id.mainContainer, args = bundle)
                addToBackStack(null)
            }
        } else {
            Log.e("CategoriesFragment", "Category with ID $categoryId not found")
        }
    }
}