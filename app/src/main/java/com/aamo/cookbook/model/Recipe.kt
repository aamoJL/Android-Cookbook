package com.aamo.cookbook.model

import java.util.UUID

data class Recipe(
  val name: String,
  val category: String,
  val subCategory: String = "",
  val servings: Int = 1,
  val chapters: Set<Chapter> = emptySet(),
  val id: UUID = UUID.randomUUID(),
){
  data class Ingredient(
    val name: String,
    val amount: Float,
    val unit: String,
    val id: UUID = UUID.randomUUID(),
  ){
    fun toFormattedString() : String{
      return if(amount == 0f){
        name
      } else{
        val formattedAmount = amount.toString()
          .trimEnd { it == '0' }.trimEnd { it == '.' }.trimEnd { it == ',' }

        "$formattedAmount $unit – $name"
      }
    }
  }

  /**
   * Part of a recipe, that can be made separately from other chapters.
   */
  data class Chapter(
    val name: String,
    val steps: Set<Step> = emptySet(),
    val id: UUID = UUID.randomUUID(),
  ){
    /**
     * Recipe step, that needs to be made in order.
     */
    data class Step(
      val description: String,
      val ingredients: Set<Ingredient> = emptySet(),
      val id: UUID = UUID.randomUUID(),
    ) {
      fun getDescriptionWithFormattedEndChar(): String {
        return "${description}${if (ingredients.isEmpty()) "." else ":"}"
      }
    }
  }
}