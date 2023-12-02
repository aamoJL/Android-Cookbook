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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.components.LabelledCheckBox
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable fun RecipeScreen(
  recipe: Recipe,
  progressChanged: (completed: Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val pagerState = rememberPagerState(pageCount = { recipe.chapters.size + 1 })
  val scope = rememberCoroutineScope()

  Column(modifier = modifier.fillMaxSize()) {
    HorizontalPager(
      pageSize = PageSize.Fill,
      state = pagerState,
      verticalAlignment = Alignment.Top,
      modifier = Modifier
        .fillMaxSize()
        .weight(1f, true)
    ) { pageIndex ->
      if (pageIndex == 0) {
        Summary(recipe = recipe)
      } else {
        Chapter(
          chapter = recipe.chapters.elementAt(pageIndex - 1),
          chapterNumber = pageIndex,
          onProgressChange = { stepIndex, value ->

          }
        )
      }
    }
    RecipeBottomBar(
      modifier = Modifier,
      onBack = { scope.launch {
        pagerState.animateScrollToPage(pagerState.currentPage - 1)
      } },
      onNext = { scope.launch {
        pagerState.animateScrollToPage(pagerState.currentPage + 1)
      } },
      backEnabled = pagerState.currentPage > 0,
      nextEnabled = pagerState.currentPage < pagerState.pageCount - 1)
  }
}

@Composable fun Summary(recipe: Recipe) {
  Column(modifier = Modifier
    .padding(8.dp)
    .fillMaxWidth()
    .verticalScroll(rememberScrollState())) {
    Text(
      text = recipe.name,
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.padding(vertical = 16.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))

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

@Composable fun Chapter(
  chapter: Recipe.Chapter,
  chapterNumber: Int,
  onProgressChange: (stepIndex: Int, value: Boolean) -> Unit
) {
  Column(modifier = Modifier
    .padding(8.dp)
    .fillMaxSize()
    .verticalScroll(rememberScrollState())) {
    Text(
      text = "${chapterNumber}. ${chapter.name}",
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.padding(vertical = 16.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
    for ((index, step) in chapter.steps.withIndex()){
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
    for (ingredient in step.ingredients){
      Text(text = ingredient.toFormattedString(),
        style = MaterialTheme.typography.bodyMedium)
    }
  }
}

@Composable
fun StepCheckBox(step: Recipe.Chapter.Step, checked: Boolean, onCheckedChange: (checked: Boolean) -> Unit, modifier: Modifier = Modifier) {
  LabelledCheckBox(
    checked = checked,
    onCheckedChange = {
      onCheckedChange(it)
    },
    label = "${step.description}${if (step.ingredients.isEmpty()) '.' else ':' }",
    modifier = modifier.fillMaxWidth()
  ){
    Column(modifier = Modifier) {
      for (ingredient in step.ingredients){
        Text(text = ingredient.toFormattedString(),
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(start = 40.dp))
      }
    }
  }
}

@Composable fun RecipeBottomBar(
  modifier: Modifier = Modifier,
  onBack: () -> Unit,
  onNext: () -> Unit,
  backEnabled: Boolean,
  nextEnabled: Boolean,
) {
  Box(modifier = modifier
    .height(70.dp)
    .background(color = MaterialTheme.colorScheme.inverseOnSurface)) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceEvenly,
      modifier = Modifier.fillMaxSize()) {
      FilledTonalButton(onClick = { onBack() }, enabled = backEnabled) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.offset(x = (-6).dp)
        ) {
          Icon(imageVector = Icons.Filled.KeyboardArrowLeft, contentDescription = null)
          Text(text = "Edellinen")
        }
      }
      Button(onClick = { onNext() }, enabled = nextEnabled) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.offset(x = 6.dp)
        ) {
          Text(text = "Seuraava")
          Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null)
        }
      }
    }
  }
}