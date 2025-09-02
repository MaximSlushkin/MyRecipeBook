package com.example.myrecipebook.ui.categories

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.myrecipebook.R
import com.example.myrecipebook.databinding.ItemCategoryBinding
import com.example.myrecipebook.databinding.ItemRecipeBinding
import com.example.myrecipebook.model.Category
import com.example.myrecipebook.model.Recipe
import java.io.IOException

class CategoriesListAdapter(private var dataSet: List<Category>) :
    RecyclerView.Adapter<CategoriesListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(categoryId: Int)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.tvCategoryTitle.text = category.title
            binding.tvCategoryDescription.text = category.description

            if (!category.imageUrl.isNullOrEmpty()) {
                val imageUrl = "https://recipes.androidsprint.ru/api/images/${category.imageUrl}"
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .override(500, 300)
                    .centerCrop()
                    .into(binding.ivCategoryImage)
            } else {

                binding.ivCategoryImage.setImageResource(R.drawable.ic_error)
            }

            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(category.id)
            }
        }
    }

    fun updateData(newCategories: List<Category>) {
        dataSet = newCategories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size
}