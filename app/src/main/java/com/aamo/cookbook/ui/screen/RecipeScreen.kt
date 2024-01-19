package com.aamo.cookbook.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.Step
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.CountInput
import com.aamo.cookbook.ui.components.LabelledCheckBox
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.toFractionFormattedString
import com.aamo.cookbook.utility.toStringWithoutZero
import com.aamo.cookbook.viewModel.RecipeScreenViewModel
import com.aamo.cookbook.viewModel.ViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun RecipeScreen(
  modifier: Modifier = Modifier,
  onBack: () -> Unit = {},
  onEditRecipe: (id: Int) -> Unit = {},
  onCopyRecipe: (id: Int) -> Unit,
  viewModel: RecipeScreenViewModel = viewModel(factory = ViewModelProvider.Factory)
) {
  val chapterUiStates by viewModel.chapterPageUiStates.collectAsStateWithLifecycle()
  val summaryUiState by viewModel.summaryPageUiStates.collectAsStateWithLifecycle()
  val servingsState by viewModel.servingsState.collectAsStateWithLifecycle()

  RecipeScreenContent(
    summaryPageUiState = summaryUiState,
    chapterUiStates = chapterUiStates,
    servingsState = servingsState,
    onBack = onBack,
    onEditRecipe = { onEditRecipe(viewModel.recipe.value.id) },
    onCopyRecipe = { onCopyRecipe(viewModel.recipe.value.id) },
    onProgressChange = { chapterId, stepId, value ->
      viewModel.updateProgress(chapterId, stepId, value)
    },
    onServingsCountChange = { viewModel.setServingsCount(it) },
    modifier = modifier
  )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeScreenContent(
  summaryPageUiState: RecipeScreenViewModel.SummaryPageUiState,
  chapterUiStates: List<RecipeScreenViewModel.ChapterPageUiState>,
  servingsState: RecipeScreenViewModel.ServingsState,
  modifier: Modifier = Modifier,
  onBack: () -> Unit = {},
  onEditRecipe: () -> Unit = {},
  onCopyRecipe: () -> Unit = {},
  onProgressChange: (chapterIndex: Int, stepIndex: Int, value: Boolean) -> Unit,
  onServingsCountChange: (count: Int) -> Unit,
) {

  val pageCount by rememberSaveable(chapterUiStates) {
    mutableIntStateOf(
      if (chapterUiStates.any { x -> x.progress.any { !it } }) chapterUiStates.size + 1
      else chapterUiStates.size + 2
    )
  }
  val pagerState = rememberPagerState(pageCount = { pageCount })
  val scope = rememberCoroutineScope()
  var moreDropMenuState by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      BasicTopAppBar(title = summaryPageUiState.recipeName, onBack = onBack) {
        Box(modifier = Modifier) {
          IconButton(onClick = { moreDropMenuState = !moreDropMenuState }) {
            Icon(
              imageVector = Icons.Filled.MoreVert,
              contentDescription = "more"
            )
          }
          DropdownMenu(
            expanded = moreDropMenuState,
            onDismissRequest = { moreDropMenuState = false }
          ) {
            DropdownMenuItem(
              leadingIcon = {
                Icon(
                  imageVector = Icons.Filled.Edit,
                  contentDescription = stringResource(R.string.description_edit_recipe)
                )
              },
              text = { Text(text = stringResource(R.string.description_edit_recipe)) },
              onClick = {
                moreDropMenuState = false
                onEditRecipe()
              }
            )
            DropdownMenuItem(
              leadingIcon = {
                Icon(
                  painter = painterResource(id = R.drawable.baseline_content_copy_24),
                  contentDescription = stringResource(R.string.description_copy_recipe)
                )
              },
              text = { Text(text = stringResource(R.string.description_copy_recipe)) },
              onClick = {
                moreDropMenuState = false
                onCopyRecipe()
              }
            )
          }
        }
      }
    }
  ) { paddingValues ->
    Surface(
      modifier = modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      Box {
        Column(modifier = Modifier) {
          HorizontalPager(
            pageSize = PageSize.Fill,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
              .fillMaxSize()
              .weight(1f, true)
              .testTag(Tags.PAGER.name)
          ) { pageIndex ->
            when (pageIndex) {
              0 -> SummaryPage(
                uiState = summaryPageUiState,
                servingsState = servingsState,
                onServingsCountChange = onServingsCountChange,
              )
              in (1..chapterUiStates.size) -> {
                val chapterIndex = pageIndex - 1
                val uiState = chapterUiStates.elementAt(chapterIndex)

                ChapterPage(
                  uiState = uiState,
                  servingsState = servingsState,
                  onProgressChange = { stepIndex, value ->
                    onProgressChange(chapterIndex, stepIndex, value)
                  }
                )
              }

              else -> CompletedPage()
            }
          }
        }
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(16.dp),
          horizontalArrangement = Arrangement.Center
        ) {
          val currentChapterIndex = chapterUiStates.indexOfFirst { state ->
            state.progress.any { !it }
          }
          val currentProgressPage = when (currentChapterIndex) {
            -1 -> chapterUiStates.size + 1
            else -> currentChapterIndex + 1
          }
          val lastPageEnabled = currentProgressPage == chapterUiStates.size + 1

          PageIndicatorItem(
            selected = pagerState.currentPage == 0,
            onClick = {
              scope.launch {
                pagerState.animateScrollToPage(0)
              }
            },
            color = MaterialTheme.colorScheme.tertiaryContainer,
            icon = Icons.Outlined.Info
          )

          repeat(chapterUiStates.size) { index ->
            val isTargetPage = currentChapterIndex == index
            PageIndicatorItem(
              selected = index + 1 == pagerState.currentPage,
              onClick = {
                scope.launch {
                  pagerState.animateScrollToPage(index + 1)
                }
              },
              isTargetPage = isTargetPage,
              color = if (isTargetPage) MaterialTheme.colorScheme.inversePrimary else
                MaterialTheme.colorScheme.secondaryContainer,
              icon = if (chapterUiStates.elementAt(index).progress.all { it }
              ) Icons.Filled.Done else null
            )
          }

          PageIndicatorItem(
            selected = pagerState.currentPage == chapterUiStates.size + 1,
            enabled = lastPageEnabled,
            onClick = {
              scope.launch {
                pagerState.animateScrollToPage(pagerState.pageCount - 1)
              }
            },
            isTargetPage = currentProgressPage == chapterUiStates.size + 1,
            color = if (lastPageEnabled) MaterialTheme.colorScheme.tertiaryContainer else
              MaterialTheme.colorScheme.onSurface,
            icon = if (lastPageEnabled) null else Icons.Filled.Lock
          )
        }
      }
    }
  }
}

@Composable
private fun PageIndicatorItem(
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isTargetPage: Boolean = false,
  enabled: Boolean = true,
  color: Color = MaterialTheme.colorScheme.secondaryContainer,
  icon: ImageVector? = null
) {
  Box(
    modifier = modifier
      .padding(10.dp)
      .clip(CircleShape)
      .background(color)
      .clickable(enabled = enabled) {
        onClick()
      }
      .size(
        width = if (isTargetPage) 48.dp else 32.dp,
        height = 32.dp
      )
  ) {
    if (icon != null) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
          .size(24.dp)
          .align(Alignment.Center)
      )
    }
    if (selected) {
      Box(
        modifier = Modifier
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.onPrimary)
          .size(16.dp)
          .align(Alignment.Center)
      ) {}
    }
  }
}

@Composable
private fun PageBase(
  title: String,
  modifier: Modifier = Modifier,
  subtitle: String = "",
  content: @Composable () -> Unit = {}
) {
  Column(
    modifier = modifier
      .padding(8.dp)
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge,
    )
    if(subtitle.isNotEmpty()){
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
      )
    }
    Spacer(modifier = Modifier.height(8.dp))
    content()
  }
}

@Composable
private fun SummaryPage(
  uiState: RecipeScreenViewModel.SummaryPageUiState,
  servingsState: RecipeScreenViewModel.ServingsState,
  onServingsCountChange: (count: Int) -> Unit,
  ) {
  PageBase(title = stringResource(R.string.page_title_ingredients)) {
      CountInput(
        value = servingsState.current,
        label = when(servingsState.multiplier) {
          1f -> stringResource(R.string.input_title_servings_without_multiplier)
          else -> stringResource(
            R.string.input_title_servings_with_multiplier,
            servingsState.multiplier.toStringWithoutZero()
          )
        },
        onValueChange = {
          onServingsCountChange(it)
        },
        minValue = 1)
    uiState.chaptersWithIngredients.forEach { chapterIngredientPair ->
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = chapterIngredientPair.first,
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(vertical = 4.dp)
        )
        Column(modifier = Modifier.width(IntrinsicSize.Max)) {
          chapterIngredientPair.second.forEach { ingredient ->
            Column(modifier = Modifier.padding(start = 16.dp)) {
              Row {
                Text(
                  text = if (ingredient.amount == 0f) "" else (ingredient.amount * servingsState.multiplier).toFractionFormattedString(),
                  style = MaterialTheme.typography.bodyMedium,
                  textAlign = TextAlign.End,
                  modifier = Modifier
                    .defaultMinSize(minWidth = 40.dp)
                    .weight(1f)
                )
                Text(
                  text = ingredient.unit,
                  style = MaterialTheme.typography.bodyMedium,
                  fontStyle = FontStyle.Italic,
                  modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                )
                Text(
                  text = ingredient.name,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.weight(5f)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CompletedPage() {
  // TODO: completed page
  // Remember to change string to resources!
  PageBase(title = "Valmis!")
}

@Composable
private fun ChapterPage(
  uiState: RecipeScreenViewModel.ChapterPageUiState,
  servingsState: RecipeScreenViewModel.ServingsState,
  onProgressChange: (stepIndex: Int, value: Boolean) -> Unit
) {
  PageBase(title = "${uiState.chapter.value.orderNumber}. ${uiState.chapter.value.name}") {
    for ((index, step) in uiState.chapter.steps.withIndex()) {
      val checked = uiState.progress.elementAtOrElse(index) { false }

      StepCheckBox(
        step = step.value,
        ingredients = step.ingredients.filter { it.stepId == step.value.id },
        servingsState = servingsState,
        checked = checked,
        modifier = Modifier.testTag(Tags.PROGRESS_CHECKBOX.name),
        onCheckedChange = { onProgressChange(index, it) })
    }
  }
}

@Composable
private fun StepCheckBox(
  step: Step,
  ingredients: List<Ingredient>,
  servingsState: RecipeScreenViewModel.ServingsState,
  checked: Boolean,
  onCheckedChange: (checked: Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  LabelledCheckBox(
    checked = checked,
    onCheckedChange = {
      onCheckedChange(it)
    },
    label = "${step.description}${if (ingredients.isEmpty()) '.' else ':'}",
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.width(IntrinsicSize.Max)) {
      for (ingredient in ingredients) {
        Row(modifier = Modifier) {
          Text(
            text = if (ingredient.amount == 0f) "" else (ingredient.amount * servingsState.multiplier).toFractionFormattedString(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            modifier = Modifier
              .defaultMinSize(minWidth = 40.dp)
              .weight(1f)
          )
          Text(
            text = ingredient.unit,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 8.dp)
          )
          Text(
            text = ingredient.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(5f)
          )
        }
      }
    }
  }
}
