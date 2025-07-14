package com.example.myrecipebook

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myrecipebook.databinding.ItemRecipeBinding
import java.io.IOException

class RecipesListAdapter(private val dataSet: List<Recipe>) :
    RecyclerView.Adapter<RecipesListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(recipeId: Int)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            binding.tvRecipeTitle.text = recipe.title

            try {
                val inputStream = itemView.context.assets.open(recipe.imageUrl)
                val drawable = Drawable.createFromStream(inputStream, null)
                binding.ivRecipeImage.setImageDrawable(drawable)
            } catch (e: IOException) {
                Log.e("RecipesAdapter", "Error loading image: ${recipe.imageUrl}", e)
            }

            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(recipe.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecipeBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size
}