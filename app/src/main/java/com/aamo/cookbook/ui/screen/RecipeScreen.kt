package com.aamo.cookbook.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.LabelledCheckBox
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.viewModel.RecipeScreenViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID

/*
TODO: configuration change changes page to 0
TODO: checkbox state changes randomly when changing configuration
*/

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RecipeScreen(
  recipeId: UUID,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: RecipeScreenViewModel = viewModel()
) {
  val pageCount by combine(viewModel.recipe, viewModel.currentProgress) { recipe, progress ->
    if (progress.sum() == recipe.chapters.sumOf { it.steps.size })
      recipe.chapters.size + 2
    else recipe.chapters.size + 1
  }.collectAsState(initial = 1)
  val pagerState = rememberPagerState(pageCount = { pageCount })
  val currentProgress by viewModel.currentProgress.collectAsState()
  val currentChapterIndex by viewModel.currentChapter.collectAsState(initial = 0)
  val scope = rememberCoroutineScope()
  val recipe by viewModel.recipe.collectAsState()

  LaunchedEffect(recipeId) {
    viewModel.getRecipe(recipeId)
  }

  Scaffold(
    topBar = { BasicTopAppBar(title = recipe.name, onBack = onBack) }
  ) {
    Surface(
      modifier = modifier
        .fillMaxSize()
        .padding(it)
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

                ChapterPage(
                  chapter = recipe.chapters.elementAt(chapterIndex),
                  chapterNumber = pageIndex,
                  onProgressChange = { _, value ->
                    if (value) viewModel.updateProgress(
                      chapterIndex,
                      viewModel.currentProgress.value[chapterIndex] + 1
                    )
                    else viewModel.updateProgress(
                      chapterIndex,
                      viewModel.currentProgress.value[chapterIndex] - 1
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

          repeat(recipe.chapters.size) { iteration ->
            val isTargetPage = currentChapterIndex == iteration
            PageIndicatorItem(
              selected = iteration + 1 == pagerState.currentPage,
              onClick = {
                scope.launch {
                  pagerState.animateScrollToPage(iteration + 1)
                }
              },
              isTargetPage = isTargetPage,
              color = if (isTargetPage) MaterialTheme.colorScheme.inversePrimary else
                MaterialTheme.colorScheme.secondaryContainer,
              icon = if (recipe.chapters.elementAt(iteration).steps.size ==
                currentProgress.elementAt(iteration)
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
private fun SummaryPage(recipe: Recipe) {
  PageBase(title = recipe.name) {
    for (chapter in recipe.chapters) {
      Text(
        text = chapter.name,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
      )
      for (step in chapter.steps) {
        Ingredients(step = step, Modifier.padding(start = 24.dp))
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
  chapter: Recipe.Chapter,
  chapterNumber: Int,
  onProgressChange: (stepIndex: Int, value: Boolean) -> Unit
) {
  PageBase(title = "${chapterNumber}. ${chapter.name}") {
    for ((index, step) in chapter.steps.withIndex()) {
      val checked = rememberSaveable(step.id) { mutableStateOf(false) }

      StepCheckBox(
        step = step,
        checked = checked.value,
        modifier = Modifier.testTag(Tags.PROGRESS_CHECKBOX.name),
        onCheckedChange = {
          checked.value = it
          onProgressChange(index, it)
        })
    }
  }
}

@Composable
private fun Ingredients(step: Recipe.Chapter.Step, modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    for (ingredient in step.ingredients) {
      Text(
        text = ingredient.toFormattedString(),
        style = MaterialTheme.typography.bodyMedium
      )
    }
  }
}

@Composable
private fun StepCheckBox(
  step: Recipe.Chapter.Step,
  checked: Boolean,
  onCheckedChange: (checked: Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  LabelledCheckBox(
    checked = checked,
    onCheckedChange = {
      onCheckedChange(it)
    },
    label = "${step.description}${if (step.ingredients.isEmpty()) '.' else ':'}",
    modifier = modifier.fillMaxWidth()
  ) {
    // TODO: styling
    Column(modifier = Modifier) {
      for (ingredient in step.ingredients) {
        Text(
          text = ingredient.toFormattedString(),
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(start = 40.dp)
        )
      }
    }
  }
}
