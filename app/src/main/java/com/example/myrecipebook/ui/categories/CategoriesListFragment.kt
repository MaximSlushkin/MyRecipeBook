package com.example.myrecipebook.ui.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.example.myrecipebook.ARG_CATEGORY_ID
import com.example.myrecipebook.ARG_CATEGORY_IMAGE_URL
import com.example.myrecipebook.ARG_CATEGORY_NAME
import com.example.myrecipebook.R
import com.example.myrecipebook.ui.recipes.recipelist.RecipesListFragment
import com.example.myrecipebook.data.STUB
import com.example.myrecipebook.databinding.FragmentListCategoriesBinding
import androidx.navigation.fragment.findNavController

class CategoriesListFragment : Fragment() {

    private lateinit var categoriesAdapter: CategoriesListAdapter
    private val viewModel: CategoriesListViewModel by viewModels()

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
        observeState()
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            state?.let {
                categoriesAdapter.updateData(it.categories)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecycler() {
        categoriesAdapter = CategoriesListAdapter(emptyList())
        binding.rvCategories.adapter = categoriesAdapter

        categoriesAdapter.setOnItemClickListener(object :
            CategoriesListAdapter.OnItemClickListener {
            override fun onItemClick(categoryId: Int) {
                openRecipesByCategoryId(categoryId)
            }
        })
    }

    private fun openRecipesByCategoryId(categoryId: Int) {

        val category = viewModel.getCategoryById(categoryId)

        if (category != null) {

            val action = CategoriesListFragmentDirections
                .actionCategoriesListFragmentToRecipesListFragment(category)
            findNavController().navigate(action)
        } else {

            throw IllegalArgumentException("Category with ID $categoryId not found")
        }
    }
}