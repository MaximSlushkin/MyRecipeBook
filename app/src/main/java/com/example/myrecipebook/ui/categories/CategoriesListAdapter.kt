package com.example.myrecipebook.ui.categories

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myrecipebook.databinding.ItemCategoryBinding
import com.example.myrecipebook.model.Category
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

            try {
                val inputStream = itemView.context.assets.open(category.imageUrl)
                val drawable = Drawable.createFromStream(inputStream, null)
                binding.ivCategoryImage.setImageDrawable(drawable)
            } catch (e: IOException) {
                Log.e(
                    "CategoriesAdapter",
                    "Error loading image: ${category.imageUrl}",
                    e
                )
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