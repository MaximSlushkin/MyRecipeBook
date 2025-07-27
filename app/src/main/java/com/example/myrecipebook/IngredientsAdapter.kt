package com.example.myrecipebook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myrecipebook.databinding.ItemIngredientBinding

class IngredientsAdapter(private val ingredients: List<Ingredient>) :
    RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

    private var portionCount: Int = 1

    fun updateIngredients(progress: Int) {
        portionCount = progress
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemIngredientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: Ingredient) {

            val quantity = ingredient.getQuantityFloat()
            val totalQuantity = quantity * portionCount

            val formattedQuantity = if (totalQuantity % 1 == 0f) {
                totalQuantity.toInt().toString()
            } else {
                "%.1f".format(totalQuantity)
            }

            binding.tvQuantityWithUnit.text = "$formattedQuantity ${ingredient.unitOfMeasure}"
            binding.tvDescription.text = ingredient.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemIngredientBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ingredients[position])
    }

    override fun getItemCount() = ingredients.size
}