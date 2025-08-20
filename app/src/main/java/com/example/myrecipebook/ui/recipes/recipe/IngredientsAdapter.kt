package com.example.myrecipebook.ui.recipes.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myrecipebook.databinding.ItemIngredientBinding
import com.example.myrecipebook.model.Ingredient
import java.math.BigDecimal
import java.math.RoundingMode

class IngredientsAdapter(
    initialIngredients: List<Ingredient>,
    initialPortionCount: Int
) : RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

    var ingredients: List<Ingredient> = initialIngredients

    var portionCount: Int = initialPortionCount

    fun updateData(
        newIngredients: List<Ingredient>,
        newPortionCount: Int
    ) {
        ingredients = newIngredients
        portionCount = newPortionCount
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding: ItemIngredientBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: Ingredient) {
            val formattedQuantity = formatQuantity(ingredient.quantity)
            binding.tvQuantityWithUnit.text = "$formattedQuantity ${ingredient.unitOfMeasure}"
            binding.tvDescription.text = ingredient.description
        }

        private fun formatQuantity(quantity: String): String {
            return runCatching {
                BigDecimal(quantity.replace(",", "."))
                    .multiply(BigDecimal(portionCount))
                    .setScale(1, RoundingMode.HALF_UP)
                    .stripTrailingZeros()
                    .toPlainString()
            }.getOrDefault(BigDecimal.ZERO)
                .toString()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemIngredientBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(ingredients[position])
    }

    override fun getItemCount() = ingredients.size
}