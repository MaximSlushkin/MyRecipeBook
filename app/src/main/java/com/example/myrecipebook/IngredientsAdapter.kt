package com.example.myrecipebook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myrecipebook.databinding.ItemIngredientBinding
import java.math.BigDecimal
import java.math.RoundingMode

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

            val quantity = try {
                BigDecimal(ingredient.quantity.replace(",", "."))
            } catch (e: Exception) {
                BigDecimal.ZERO
            }

            val totalQuantity = quantity.multiply(BigDecimal(portionCount))

            val formattedQuantity = if (totalQuantity.scale() <= 0) {
                totalQuantity.toInt().toString()
            } else {
                totalQuantity.setScale(1, RoundingMode.HALF_UP).toString()
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