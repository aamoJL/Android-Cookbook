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
//  bodyLarge = Typography().bodyLarge.copy(
//    fontFamily = Caveat,
//  ),
//  titleLarge = Typography().titleLarge.copy(
//    fontFamily = Caveat,
//  ),
//  labelSmall = Typography().labelSmall.copy(
//    fontFamily = Caveat,
//  ),
  headlineLarge = Typography().headlineLarge.copy(
    fontFamily = Caveat,
  ),
  headlineMedium = Typography().headlineMedium.copy(
    fontFamily = Caveat,
  )
)


