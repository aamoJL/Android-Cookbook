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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aamo.cookbook.R
import com.aamo.cookbook.model.ChapterWithStepsAndIngredients
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.model.Step
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.LabelledCheckBox
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.toFractionFormattedString
import com.aamo.cookbook.viewModel.RecipeScreenViewModel
import com.aamo.cookbook.viewModel.ViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RecipeScreen(
  modifier: Modifier = Modifier,
  onBack: () -> Unit = {},
  onEditRecipe: (id: Int) -> Unit = {},
  viewModel: RecipeScreenViewModel = viewModel(factory = ViewModelProvider.Factory)
) {
  val recipe = viewModel.recipe
  val currentProgress by viewModel.currentProgress.collectAsState()
  val pageCount by rememberSaveable(currentProgress) {
    mutableIntStateOf(
      if (currentProgress.any { x -> x.any { !it } }) recipe.chapters.size + 1
      else recipe.chapters.size + 2
    )
  }
  val pagerState = rememberPagerState(pageCount = { pageCount })
  val scope = rememberCoroutineScope()

  Scaffold(
    topBar = {
      BasicTopAppBar(title = recipe.value.name, onBack = onBack) {
        IconButton(onClick = { onEditRecipe(recipe.value.id) }) {
          Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = stringResource(R.string.description_edit_recipe)
          )
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
              0 -> SummaryPage(recipe = recipe)
              in (1..recipe.chapters.size) -> {
                val chapterIndex = pageIndex - 1
                val chapter = recipe.chapters.elementAt(chapterIndex)

                ChapterPage(
                  chapter = chapter,
                  chapterProgress = currentProgress.elementAtOrElse(chapterIndex){ emptyList() },
                  onProgressChange = { stepIndex, value ->
                    viewModel.updateProgress(
                      chapterIndex = chapterIndex,
                      stepIndex = stepIndex,
                      value = value
                    )
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
          val currentChapterIndex = currentProgress.indexOfFirst { progress ->
            progress.any { x -> !x }
          }
          val currentProgressPage = when (currentChapterIndex) {
            -1 -> recipe.chapters.size + 1
            else -> currentChapterIndex + 1
          }
          val lastPageEnabled = currentProgressPage == recipe.chapters.size + 1

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

          repeat(recipe.chapters.size) { index ->
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
              icon = if (currentProgress.elementAtOrElse(index){ emptyList() }.all { it }
              ) Icons.Filled.Done else null
            )
          }

          PageIndicatorItem(
            selected = pagerState.currentPage == recipe.chapters.size + 1,
            enabled = lastPageEnabled,
            onClick = {
              scope.launch {
                pagerState.animateScrollToPage(pagerState.pageCount - 1)
              }
            },
            isTargetPage = currentProgressPage == recipe.chapters.size + 1,
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
      modifier = Modifier.padding(vertical = 16.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
    content()
  }
}

@Composable
private fun SummaryPage(
  recipe: RecipeWithChaptersStepsAndIngredients
) {
  PageBase(title = recipe.value.name) {
    recipe.chapters.forEach { chapter ->
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = chapter.value.name,
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(vertical = 4.dp)
        )
        Column(modifier = Modifier.width(IntrinsicSize.Max)) {
          chapter.steps.forEach { step ->
            Column(modifier = Modifier.padding(start = 16.dp)) {
              step.ingredients.forEach { ingredient ->
                Row {
                  Text(
                    text = if (ingredient.amount == 0f) "" else ingredient.amount.toFractionFormattedString(),
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
}

@Composable
private fun CompletedPage() {
  // TODO: completed page
  // Remember to change string to resources!
  PageBase(title = "Valmis!")
}

@Composable
private fun ChapterPage(
  chapter: ChapterWithStepsAndIngredients,
  chapterProgress: List<Boolean>,
  onProgressChange: (stepIndex: Int, value: Boolean) -> Unit
) {
  PageBase(title = "${chapter.value.orderNumber}. ${chapter.value.name}") {
    for ((index, step) in chapter.steps.withIndex()) {
      val checked = chapterProgress.elementAtOrElse(index) { false }

      StepCheckBox(
        step = step.value,
        ingredients = step.ingredients.filter { it.stepId == step.value.id },
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
            text = if (ingredient.amount == 0f) "" else ingredient.amount.toFractionFormattedString(),
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
