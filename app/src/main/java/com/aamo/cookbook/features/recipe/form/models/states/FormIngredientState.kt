package com.aamo.cookbook.features.recipe.form.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.utility.extensions.general.Zero
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import java.util.UUID

class FormIngredientState(
  private val model: Ingredient = Ingredient(),
  val guid: UUID,
  val onChange: () -> Unit = {},
  onValidityChanged: () -> Unit = {},
) {
  class Fields(private val model: Ingredient = Ingredient(), private val onChange: () -> Unit) {
    val name = ViewModelState(model.name).onChange { onChange() }
    val amount =
      ViewModelState<Double?>(model.amount).onChange { onChange() }.transformation { value ->
        if (value != null && value <= 0) null else value
      }
    val unit = ViewModelState(model.unit).onChange { onChange() }

    var requiredFieldsFilled by mutableStateOf(requiredFieldsFilled())
      private set

    private fun onChange() {
      requiredFieldsFilled = requiredFieldsFilled()
      onChange.invoke()
    }

    private fun requiredFieldsFilled(): Boolean {
      if (name.value.isBlank()) return false
      if (amount.value?.let { it < 0 } == true) return false
      return true
    }

    fun getModel(): Ingredient {
      return model.copy(
        name = name.value,
        amount = amount.value ?: Double.Zero,
        unit = unit.value,
      )
    }
  }

  val fields = Fields(model = model, onChange = { onChange() })
  val validity = ViewModelState(checkValidity()).onChange { onValidityChanged() }

  private fun onChange() {
    validity.update(checkValidity())
    onChange.invoke()
  }

  private fun checkValidity(): Boolean {
    return fields.requiredFieldsFilled
  }

  fun getModel(): Ingredient {
    return fields.getModel()
  }
}