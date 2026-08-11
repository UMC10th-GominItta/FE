package com.gominitta.android.presentation.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gominitta.android.navigation.Routes
import com.gominitta.android.presentation.home.HomeScreen
import com.gominitta.android.presentation.main.components.GominittaBottomBar
import com.gominitta.android.presentation.recipe.RecipeCenterScreen
import com.gominitta.android.presentation.report.ReportRoute
import com.gominitta.android.presentation.session.SessionListScreen

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Text
import com.gominitta.android.presentation.recipe.RecipeCreateScreen
import com.gominitta.android.presentation.recipe.RecipeEditScreen
import com.gominitta.android.presentation.recipe.RecipeRunScreen
import com.gominitta.android.presentation.recipe.RecipeCompleteScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.gominitta.android.presentation.recipe.RecipeCenterViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

private const val HOME_NEXT_SESSION_PLACEHOLDER_ID = 1L

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
    onNavigateToWorryMemo: (Long) -> Unit,
    onNavigateToSessionDetail: (Long) -> Unit,
    onNavigateToSessionEdit: (Long) -> Unit,
    onNavigateToSessionResult: (Long) -> Unit,
    onNavigateToMyPage: () -> Unit,
    startTab: String = Routes.HOME,
    onNavigateBackToSession: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val tabNavController = rememberNavController()
    val cameFromSession = startTab == Routes.RECIPE

    LaunchedEffect(startTab) {
        if (startTab != Routes.HOME) {
            tabNavController.navigate(startTab) { launchSingleTop = true }
        }
    }

    val currentTabRoute by tabNavController.currentBackStackEntryAsState() // 추가
    val showBottomBar = currentTabRoute?.destination?.route !in setOf( // 변경
        Routes.RECIPE_RUN,
        Routes.RECIPE_COMPLETE,
    )
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
            // 화면 전환 시 배경 투명 화면들이 겹쳐 보이는 "잔상" 방지용 짧은 크로스페이드.
            enterTransition = { fadeIn(tween(150)) },
            exitTransition = { fadeOut(tween(150)) },
            popEnterTransition = { fadeIn(tween(150)) },
            popExitTransition = { fadeOut(tween(150)) },
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToWorryInput = onNavigateToWorryInput,
                    onNavigateToWorryMemo = onNavigateToWorryMemo,
                    // 홈 화면의 "다음 세션" 카드가 아직 실제 세션 데이터에 연결되지 않아
                    // (하드코딩된 표시값) sessionId 도 임시로 고정값을 쓴다. 홈 카드가
                    // 실데이터를 받으면 그 세션 id를 그대로 넘기면 된다.
                    onNavigateToSessionDetail = { onNavigateToSessionDetail(HOME_NEXT_SESSION_PLACEHOLDER_ID) },
                    onNavigateToSessionList = {
                        tabNavController.navigate(Routes.SESSION_LIST) {
                            popUpTo(tabNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToMyPage = onNavigateToMyPage,
                )
            }
            composable(Routes.SESSION_LIST) {
                SessionListScreen(
                    onNavigateToSessionDetail = onNavigateToSessionDetail,
                    onNavigateToSessionEdit = onNavigateToSessionEdit,
                    onNavigateToSessionResult = onNavigateToSessionResult,
                    onNavigateToWorryInput = onNavigateToWorryInput,
                    onNavigateToWorryMemo = onNavigateToWorryMemo,
                )
            }
            composable(Routes.RECIPE) {
                val recipeCenterViewModel: RecipeCenterViewModel = hiltViewModel()
                val lifecycleOwner = LocalLifecycleOwner.current

                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            recipeCenterViewModel.refresh()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                }
                RecipeCenterScreen(
                    recipes = recipeCenterViewModel.recipes,
                    onNavigateBack = { if (cameFromSession) onNavigateBackToSession() },
                    onCreateClick = {
                        tabNavController.navigate(Routes.RECIPE_CREATE)
                    },
                    onRecipeClick = { recipeId ->
                        tabNavController.navigate(Routes.recipeRunRoute(recipeId))
                    },
                    onEditClick = { recipeId ->
                        tabNavController.navigate(Routes.recipeEditRoute(recipeId))
                    },
                    onDeleteClick = { recipeId ->
                        recipeCenterViewModel.deleteRecipe(recipeId)
                    },
                )
            }

            composable(Routes.RECIPE_CREATE) {
                RecipeCreateScreen(
                    onNavigateBack = {
                        tabNavController.popBackStack()
                    },
                )
            }

            composable(
                route = Routes.RECIPE_RUN,
                arguments = listOf(navArgument("recipeId") { type = NavType.LongType }),
            ) {
                RecipeRunScreen(
                    onNavigateBack = {
                        tabNavController.popBackStack()
                    },
                    onFinishClick = {
                        tabNavController.navigate(Routes.RECIPE_COMPLETE)
                    },
                )
            }

            composable(
                route = Routes.RECIPE_EDIT,
                arguments = listOf(navArgument("recipeId") { type = NavType.LongType }),
            ) {
                RecipeEditScreen(
                    onNavigateBack = {
                        tabNavController.popBackStack()
                    },
                )
            }

            composable(Routes.RECIPE_COMPLETE) {
                RecipeCompleteScreen(
                    onFinishClick = {
                        tabNavController.popBackStack(Routes.RECIPE, inclusive = false)
                    },
                )
            }

            composable(Routes.REPORT) {
                ReportRoute()
            }
        }
    }
}
