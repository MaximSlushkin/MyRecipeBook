package com.example.myrecipebook.ui.categories

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
import com.example.myrecipebook.ARG_CATEGORY_ID
import com.example.myrecipebook.ARG_CATEGORY_IMAGE_URL
import com.example.myrecipebook.ARG_CATEGORY_NAME
import com.example.myrecipebook.R
import com.example.myrecipebook.ui.recipes.recipelist.RecipesListFragment
import com.example.myrecipebook.databinding.FragmentListCategoriesBinding
import androidx.navigation.fragment.findNavController

class CategoriesListFragment : Fragment() {

    private lateinit var categoriesAdapter: CategoriesListAdapter
    private val viewModel: CategoriesListViewModel by viewModels()

    private var _binding: FragmentListCategoriesBinding? = null
    private val binding: FragmentListCategoriesBinding
        get() = checkNotNull(_binding) {
            "Binding is null. Fragment may have been destroyed or not initialized properly."
        }

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
        observeToastEvents()
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (_binding == null) return@observe
            state?.let {
                if (state.isError) {

                    categoriesAdapter.updateData(emptyList())
                } else {
                    categoriesAdapter.updateData(state.categories)
                }
            }
        }
    }

    private fun observeToastEvents() {
        viewModel.toastEvent.observe(viewLifecycleOwner) { message ->
            if (_binding == null) return@observe
            message?.let {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                viewModel.clearToastEvent()
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