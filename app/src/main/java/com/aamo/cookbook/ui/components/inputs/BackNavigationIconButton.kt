package com.aamo.cookbook.ui.components.inputs

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.aamo.cookbook.R
import com.aamo.cookbook.utility.tags.UITag

@Composable
fun BackNavigationIconButton(onBack: () -> Unit) {
  IconButton(onClick = onBack, modifier = Modifier.testTag(UITag.BACK_BUTTON.name)) {
    Icon(
      painter = painterResource(R.drawable.rounded_arrow_back_24),
      contentDescription = stringResource(R.string.cd_navigate_back)
    )
  }
}