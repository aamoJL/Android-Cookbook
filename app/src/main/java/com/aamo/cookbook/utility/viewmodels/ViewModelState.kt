package com.aamo.cookbook.utility.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ViewModelState<T>(initValue: T) {
  var value by mutableStateOf(initValue)
    private set

  private var onChange: ((T) -> Unit)? = null
  private var transformationPredicate: ((T) -> T)? = null
  private var validationPredicate: ((T) -> Boolean)? = null

  fun update(value: T): T? {
    val newValue = transformationPredicate?.invoke(value) ?: value

    if (this.value != newValue && validationPredicate?.invoke(newValue) != false) {
      this.value = newValue
      onChange?.invoke(this.value)
    }

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
  // TODO: unit test
  fun transformation(predicate: (T) -> T): ViewModelState<T> {
    transformationPredicate = predicate
    return this
  }

  /**
   * Adds validation predicate to the state
   */
  // TODO: unit test
  fun validation(predicate: (T) -> Boolean): ViewModelState<T> {
    validationPredicate = predicate
    return this
  }
}