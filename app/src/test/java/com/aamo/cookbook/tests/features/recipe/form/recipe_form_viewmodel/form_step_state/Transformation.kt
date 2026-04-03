package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_step_state

import com.aamo.cookbook.features.recipe.form.models.states.FormStepState
import org.junit.Assert
import org.junit.Test
import java.util.UUID

class Transformation {
  @Test
  fun timerMinutes() {
    Assert.assertNull(
      FormStepState(
        guid = UUID.randomUUID(),
        onValidityChanged = { }).fields.apply {
        timerMinutes.update(0)
      }.timerMinutes.value
    )

    Assert.assertNull(
      FormStepState(
        guid = UUID.randomUUID(),
        onValidityChanged = { }).fields.apply {
        timerMinutes.update(null)
      }.timerMinutes.value
    )

    Assert.assertNull(
      FormStepState(
        guid = UUID.randomUUID(),
        onValidityChanged = { }).fields.apply {
        timerMinutes.update(-1)
      }.timerMinutes.value
    )

    Assert.assertEquals(
      123,
      FormStepState(guid = UUID.randomUUID(), onValidityChanged = { }).fields.apply {
        timerMinutes.update(123)
      }.timerMinutes.value
    )
  }
}