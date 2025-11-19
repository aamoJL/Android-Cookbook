package com.aamo.cookbook.ui.screen.recipeScreen

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aamo.cookbook.R
import com.aamo.cookbook.service.IOService
import com.aamo.cookbook.utility.SnackbarProperties
import com.aamo.cookbook.viewModel.RecipeScreenViewModel

@Composable
fun RecipeScreen(
  modifier: Modifier = Modifier,
  onBack: () -> Unit = {},
  onEditRecipe: (id: Int) -> Unit,
  onCopyRecipe: (id: Int) -> Unit,
  onShowSnackbar: (SnackbarProperties) -> Unit = {},
  viewModel: RecipeScreenViewModel
) {
  val chapterUiStates by viewModel.chapterPageUiStates.collectAsStateWithLifecycle()
  val summaryUiState by viewModel.summaryPageUiStates.collectAsStateWithLifecycle()
  val completedUiState by viewModel.completedPageUiStates.collectAsStateWithLifecycle()
  val servingsState by viewModel.servingsState.collectAsStateWithLifecycle()
  val favoriteState by viewModel.favoriteState.collectAsStateWithLifecycle()
  val context = LocalContext.current

  RecipeScreenContent(
    summaryPageUiState = summaryUiState,
    chapterPageUiStates = chapterUiStates,
    completedPageUiState = completedUiState,
    servingsState = servingsState,
    bookmarked = favoriteState,
    modifier = modifier,
    onBack = onBack,
    onEditRecipe = { onEditRecipe(viewModel.recipeId) },
    onCopyRecipe = { onCopyRecipe(viewModel.recipeId) },
    onProgressChange = { chapterId, stepId, value ->
      viewModel.updateProgress(chapterId, stepId, value)
    },
    onServingsCountChange = { viewModel.setServingsCount(it) },
    onFavoriteChange = {
      viewModel.setFavoriteState(it)
      onShowSnackbar(
        SnackbarProperties(
          if (it) context.getString(R.string.snackbar_recipe_added_to_favorites)
          else context.getString(R.string.snackbar_recipe_removed_from_favorites)
        )
      )
    },
    onRatingChange = { viewModel.setRating(it) },
    onThumbnailChange = {
      viewModel.setThumbnail(
        IOService(context).getFileNameWithSuffixFromUri(it) ?: ""
      )
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeScreenContent(
  summaryPageUiState: RecipeScreenViewModel.SummaryPageUiState,
  chapterPageUiStates: List<RecipeScreenViewModel.ChapterPageUiState>,
  completedPageUiState: RecipeScreenViewModel.CompletedPageUiState,
  servingsState: RecipeScreenViewModel.ServingsState,
  bookmarked: Boolean,
  modifier: Modifier = Modifier,
  onBack: () -> Unit = {},
  onEditRecipe: () -> Unit = {},
  onCopyRecipe: () -> Unit = {},
  onProgressChange: (chapterIndex: Int, stepIndex: Int, value: Boolean) -> Unit,
  onServingsCountChange: (count: Int) -> Unit,
  onFavoriteChange: (Boolean) -> Unit,
  onRatingChange: (Int) -> Unit,
  onThumbnailChange: (Uri) -> Unit,
) {
  val pageCount = rememberSaveable(chapterPageUiStates) { chapterPageUiStates.size + 2 }
  val pagerState = rememberPagerState(pageCount = { pageCount }, initialPage = 1)

  Scaffold(
    topBar = {}) { paddingValues ->
    Surface(
      modifier = modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      Column {
        Column(modifier = Modifier.weight(1f, true)) {
          HorizontalPager(
            pageSize = PageSize.Fill,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
              .fillMaxSize()
              .weight(1f, true)
          ) { pageIndex ->
            when (pageIndex) {
//              0 -> CompletedPage(
//                uiState = completedPageUiState,
//                onRatingChange = onRatingChange,
//                onThumbnailChange = onThumbnailChange
//              )

//              1 -> SummaryPage(
//                uiState = summaryPageUiState,
//                servingsState = servingsState,
//                onServingsCountChange = onServingsCountChange,
//              )

              in (2..chapterPageUiStates.size + 1) -> {
                val chapterIndex = pageIndex - 2
                val uiState = chapterPageUiStates.elementAt(chapterIndex)

                ChapterPage(
                  uiState = uiState,
                  servingsState = servingsState,
                  onProgressChange = { stepIndex, value ->
                    onProgressChange(chapterIndex, stepIndex, value)
                  },
                )
              }

              else -> {}
            }
          }
        }
      }
    }
  }
}