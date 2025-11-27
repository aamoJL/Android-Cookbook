package com.aamo.cookbook.features.recipe.view.models

import com.aamo.cookbook.utility.viewmodels.ViewModelState

class ServingsState {
  val baseline = ViewModelState(1).validation { it > 0 }
  val current = ViewModelState(1).validation { it > 0 }

  val multiplier: Double get() = current.value.toDouble() / baseline.value.toDouble()
}