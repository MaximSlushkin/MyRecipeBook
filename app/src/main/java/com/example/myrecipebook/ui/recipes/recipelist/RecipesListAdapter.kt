package com.example.myrecipebook.ui.recipes.recipelist

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.myrecipebook.R
import com.example.myrecipebook.databinding.ItemRecipeBinding
import com.example.myrecipebook.model.Recipe
import java.io.IOException

class RecipesListAdapter(private var dataSet: List<Recipe>) :
    RecyclerView.Adapter<RecipesListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(recipeId: Int)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    fun updateData(newRecipes: List<Recipe>) {
        dataSet = newRecipes
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            binding.tvRecipeTitle.text = recipe.title

            val imageUrl = "https://recipes.androidsprint.ru/api/images/${recipe.imageUrl}"
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .override(600, 400)
                .centerCrop()
                .into(binding.ivRecipeImage)

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