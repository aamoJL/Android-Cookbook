package com.aamo.cookbook.utility.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aamo.cookbook.utility.extensions.general.onFalse

class ViewModelState<T>(initValue: T) {
  var value by mutableStateOf(initValue)
    private set

  private var onChange: ((T) -> Unit)? = null
  private var transformationPredicate: ((T) -> T)? = null
  private var validationPredicate: ((T) -> Boolean)? = null

  fun update(value: T): T {
    val newValue = transformationPredicate.let {
      if (it == null) value
      else it.invoke(value)
    }

    if (this.value == newValue) return this.value

    validationPredicate?.invoke(newValue)?.onFalse { return this.value }

    this.value = newValue
    onChange?.invoke(this.value)

    return this.value
  }

  /**
   * Adds change function to the state
   */
  fun onChange(function: (T) -> Unit): ViewModelState<T> {
    onChange = function
    return this
  }

  /**
   * Adds transformation predicate to the state
   */
  fun transformation(predicate: (T) -> T): ViewModelState<T> {
    transformationPredicate = predicate
    return this
  }

  /**
   * Adds validation predicate to the state
   */
  fun validation(predicate: (T) -> Boolean): ViewModelState<T> {
    validationPredicate = predicate
    return this
  }
}