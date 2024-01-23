package com.aamo.cookbook.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.aamo.cookbook.R

val Caveat = FontFamily(
  Font(R.font.caveat)
)

// Set of Material typography styles to start with
val Typography = Typography(
  // Headline
  headlineLarge = Typography().headlineLarge.copy(
    fontFamily = Caveat,
  ),
  headlineMedium = Typography().headlineMedium.copy(
    fontFamily = Caveat,
  ),
  // Title
  titleMedium = Typography().titleMedium.copy(
    fontFamily = Caveat
  )
)


