@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.ui.components.inputs.text_field

import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Suppress("unused")
@Composable
fun borderlessTextFieldColors() = TextFieldDefaults.colors(
  focusedContainerColor = Color.Transparent,
  unfocusedContainerColor = Color.Transparent,
)