package com.example.myrecipebook.ui.recipes.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myrecipebook.databinding.ItemIngredientBinding
import com.example.myrecipebook.model.Ingredient
import java.math.BigDecimal
import java.math.RoundingMode

class IngredientsAdapter(
    private var ingredients: List<Ingredient>,
    private var portionCount: Int,
) : RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {
    fun updateData(
        newIngredients: List<Ingredient>,
        newPortionCount: Int,
    ) {
        ingredients = newIngredients
        portionCount = newPortionCount
        notifyDataSetChanged()
    }

    fun updateIngredients(progress: Int) {
        portionCount = progress
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding: ItemIngredientBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ingredient: Ingredient) {
            val formattedQuantity =
                runCatching {
                    BigDecimal(ingredient.quantity.replace(",", "."))
                }.getOrDefault(BigDecimal.ZERO)
                    .multiply(BigDecimal(portionCount))
                    .setScale(1, RoundingMode.HALF_UP)
                    .stripTrailingZeros()
                    .toPlainString()

            binding.tvQuantityWithUnit.text = "$formattedQuantity ${ingredient.unitOfMeasure}"
            binding.tvDescription.text = ingredient.description
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemIngredientBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(ingredients[position])
    }

    override fun getItemCount() = ingredients.size
}
