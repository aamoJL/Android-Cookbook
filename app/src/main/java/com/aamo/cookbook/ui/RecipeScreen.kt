package com.aamo.cookbook.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.components.LabelledCheckBox
import kotlinx.coroutines.launch

class RecipeScreen {
  data class NavButtonProperties(
    val visible: Boolean,
    val enabled: Boolean,
    val highlighted: Boolean,
    val title: String,
    val onClick: () -> Unit
  )

  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  fun Screen(
    recipe: Recipe,
    modifier: Modifier = Modifier
  ) {
    val pagerState = rememberPagerState(pageCount = { recipe.chapters.size + 2 })
    val scope = rememberCoroutineScope()
    val currentProgress = rememberSaveable {
      recipe.chapters.map {
        mutableIntStateOf(0)
      }
    }

    Column(modifier = modifier.fillMaxSize()) {
      HorizontalPager(
        pageSize = PageSize.Fill,
        state = pagerState,
        verticalAlignment = Alignment.Top,
        modifier = Modifier
          .fillMaxSize()
          .weight(1f, true)
      ) { pageIndex ->
        when {
          pageIndex == 0 -> SummaryPage(recipe = recipe)
          pageIndex < pagerState.pageCount - 1 -> {
            val chapterIndex = pageIndex - 1

            ChapterPage(
              chapter = recipe.chapters.elementAt(chapterIndex),
              chapterNumber = pageIndex,
              onProgressChange = { _, value ->
                if (value)
                  currentProgress[chapterIndex].intValue += 1
                else
                  currentProgress[chapterIndex].intValue -= 1
              }
            )
          }

          else -> CompletedPage()
        }
      }
      BottomBar(modifier = Modifier) {
        /**
         * Index of the first chapter that is in progress.
         * Index will be -1 if all chapters are completed
         */
        val incompleteChapterIndex = currentProgress.withIndex()
          .indexOfFirst { (i, x) -> recipe.chapters.elementAt(i).steps.size > x.intValue }
        val targetPage = when (incompleteChapterIndex) {
          -1 -> pagerState.pageCount - 1
          else -> incompleteChapterIndex + 1
        }

        NavBarButtons(
          previousButtonProperties = NavButtonProperties(
            visible = pagerState.currentPage != 0,
            enabled = pagerState.currentPage != 0,
            highlighted = targetPage - pagerState.currentPage < 0,
            title = "Edellinen",
            onClick = {
              scope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
              }
            }
          ),
          nextButtonProperties = NavButtonProperties(
            visible = pagerState.currentPage != pagerState.pageCount - 1,
            enabled = !(pagerState.pageCount - 2 == pagerState.currentPage && incompleteChapterIndex != -1),
            highlighted = targetPage - pagerState.currentPage > 0,
            title = when {
              pagerState.currentPage == 0 && !currentProgress.any { x -> x.intValue != 0 } -> "Aloita"
              pagerState.currentPage >= pagerState.pageCount - 2 -> "Valmis"
              else -> "Seuraava"
            },
            onClick = {
              scope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
              }
            }
          ),
          isCompleted = incompleteChapterIndex == -1
        )
      }
    }
  }

  @Composable
  fun PageBase(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
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
  fun SummaryPage(recipe: Recipe) {
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
  fun CompletedPage() {
    PageBase(title = "Valmis!")
  }

  @Composable
  fun ChapterPage(
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
          modifier = Modifier,
          onCheckedChange = {
            checked.value = it
            onProgressChange(index, it)
          })
      }
    }
  }

  @Composable
  fun Ingredients(step: Recipe.Chapter.Step, modifier: Modifier = Modifier) {
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
  fun StepCheckBox(
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

  @Composable
  fun BottomBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
  ) {
    Box(
      modifier = modifier
        .height(70.dp)
        .background(color = MaterialTheme.colorScheme.inverseOnSurface)
    ) {
      content()
    }
  }

  @Composable
  fun NavBarButtons(
    previousButtonProperties: NavButtonProperties,
    nextButtonProperties: NavButtonProperties,
    isCompleted: Boolean,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(24.dp),
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
    ) {
      // PREVIOUS-BUTTON
      if (previousButtonProperties.visible) {
        Button(
          colors = when {
            previousButtonProperties.highlighted -> ButtonDefaults.buttonColors()
            else -> ButtonDefaults.filledTonalButtonColors()
          },
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
          onClick = { previousButtonProperties.onClick() },
          enabled = previousButtonProperties.enabled,
          modifier = Modifier.weight(1f)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.offset(x = (-6).dp)
          ) {
            Icon(imageVector = Icons.Filled.KeyboardArrowLeft, contentDescription = null)
            Text(text = previousButtonProperties.title)
          }
        }
      }

      // NEXT-BUTTON
      if (nextButtonProperties.visible) {
        Button(
          colors = when {
            isCompleted && nextButtonProperties.highlighted ->
              ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            nextButtonProperties.highlighted -> ButtonDefaults.buttonColors()
            else -> ButtonDefaults.filledTonalButtonColors()
          },
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
          onClick = { nextButtonProperties.onClick() },
          enabled = nextButtonProperties.enabled,
          modifier = Modifier.weight(1f)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.offset(x = 6.dp)
          ) {
            Text(text = nextButtonProperties.title)
            Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null)
          }
        }
      }
    }
  }
}