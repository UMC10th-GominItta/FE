package com.gominitta.android.presentation.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gominitta.android.navigation.Routes
import com.gominitta.android.presentation.home.HomeScreen
import com.gominitta.android.presentation.main.components.GominittaBottomBar
import com.gominitta.android.presentation.recipe.RecipeCenterScreen
import com.gominitta.android.presentation.report.ReportScreen
import com.gominitta.android.presentation.session.SessionListScreen

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Text
import com.gominitta.android.presentation.recipe.RecipeCreateScreen
import com.gominitta.android.presentation.recipe.RecipeEditScreen
import com.gominitta.android.presentation.recipe.RecipeRunScreen
import com.gominitta.android.presentation.recipe.RecipeViewModel

/**
 * 하단 탭 바를 가진 메인 컨테이너.
 *
 * 안쪽 NavHost(tabNavController)가 4개 탭(홈/마음세션/마음레시피/리포트)을 전환하고,
 * 바텀바는 고정. 걱정예약·마음세션 등 전체화면 플로우는 바깥 navController 로 이동하도록
 * 콜백으로 위임받는다(바텀바 없이 전체화면으로 뜸).
 */
@Composable
fun MainScreen(
    onNavigateToWorryInput: () -> Unit,
    onNavigateToWorryMemo: () -> Unit,
    onNavigateToSessionDetail: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabNavController = rememberNavController()

    val recipeViewModel: RecipeViewModel = viewModel()
    val recipeUiState = recipeViewModel.uiState

    Scaffold(
        modifier = modifier,
        bottomBar = { GominittaBottomBar(tabNavController) },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToWorryInput = onNavigateToWorryInput,
                    onNavigateToWorryMemo = onNavigateToWorryMemo,
                    onNavigateToSessionDetail = onNavigateToSessionDetail,
                    onNavigateToMyPage = onNavigateToMyPage,
                )
            }
            composable(Routes.SESSION_LIST) {
                SessionListScreen(
                    onNavigateToSessionDetail = onNavigateToSessionDetail,
                    onNavigateBack = {},
                )
            }
            composable(Routes.RECIPE) {
                RecipeCenterScreen(
                    recipes = recipeUiState.recipes,
                    onNavigateBack = {},
                    onCreateClick = {
                        tabNavController.navigate(Routes.RECIPE_CREATE)
                    },
                    onRecipeClick = { recipeId ->
                        recipeViewModel.selectRecipe(recipeId)
                        tabNavController.navigate(Routes.RECIPE_RUN)
                    },
                    onEditClick = { recipeId ->
                        recipeViewModel.selectRecipe(recipeId)
                        tabNavController.navigate(Routes.RECIPE_EDIT)
                    },
                    onDeleteClick = { recipeId ->
                        recipeViewModel.deleteRecipe(recipeId)
                    },
                )
            }

            composable(Routes.RECIPE_CREATE) {
                RecipeCreateScreen(
                    onNavigateBack = {
                        tabNavController.popBackStack()
                    },
                    onRegisterClick = { title, description, durationMinutes ->
                        recipeViewModel.createRecipe(
                            title = title,
                            description = description,
                            durationMinutes = durationMinutes,
                        )
                        tabNavController.popBackStack()
                    },
                )
            }

            composable(Routes.RECIPE_RUN) {
                val selectedRecipe = recipeUiState.selectedRecipe

                if (selectedRecipe != null) {
                    RecipeRunScreen(
                        recipe = selectedRecipe,
                        onNavigateBack = {
                            tabNavController.popBackStack()
                        },
                        onFinishClick = {
                            tabNavController.popBackStack()
                        },
                    )
                } else {
                    Text(text = "선택된 레시피가 없습니다.")
                }
            }

            composable(Routes.RECIPE_EDIT) {
                val selectedRecipe = recipeUiState.selectedRecipe

                if (selectedRecipe != null) {
                    RecipeEditScreen(
                        recipe = selectedRecipe,
                        onNavigateBack = {
                            tabNavController.popBackStack()
                        },
                        onDeleteClick = { recipeId ->
                            recipeViewModel.deleteRecipe(recipeId)
                            tabNavController.popBackStack()
                        },
                        onCompleteClick = { recipeId, title, description, durationMinutes ->
                            recipeViewModel.updateRecipe(
                                recipeId = recipeId,
                                title = title,
                                description = description,
                                durationMinutes = durationMinutes,
                            )
                            tabNavController.popBackStack()
                        },
                    )
                } else {
                    Text(text = "선택된 레시피가 없습니다.")
                }
            }
            composable(Routes.REPORT) {
                ReportScreen(onNavigateBack = {})
            }
        }
    }
}
