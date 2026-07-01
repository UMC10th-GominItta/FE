package com.gominitta.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gominitta.android.presentation.home.HomeScreen
import com.gominitta.android.presentation.mypage.MyPageScreen
import com.gominitta.android.presentation.onboarding.LoginScreen
import com.gominitta.android.presentation.onboarding.OnboardingScreen
import com.gominitta.android.presentation.onboarding.SplashScreen
import com.gominitta.android.presentation.recipe.RecipeScreen
import com.gominitta.android.presentation.report.ReportScreen
import com.gominitta.android.presentation.session.SessionActiveScreen
import com.gominitta.android.presentation.session.SessionCompleteScreen
import com.gominitta.android.presentation.session.SessionDetailScreen
import com.gominitta.android.presentation.session.SessionListScreen
import com.gominitta.android.presentation.session.SessionRatingScreen
import com.gominitta.android.presentation.worry.WorryInputScreen
import com.gominitta.android.presentation.worry.WorryIntensityScreen
import com.gominitta.android.presentation.worry.WorryMemoScreen
import com.gominitta.android.presentation.worry.WorryScheduleScreen
import com.gominitta.android.presentation.worry.WorrySavedScreen

/**
 * Root navigation graph — the ONLY place holding a [NavHostController].
 * Screens receive plain lambda callbacks; route strings come from [Routes].
 *
 * NOTE: The 4 bottom-tab destinations (HOME / SESSION_LIST / RECIPE / REPORT)
 * are registered as flat destinations for now. A real BottomNavigationBar with
 * a nested graph should host them later — this is a click-through stub.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        // ── 온보딩 · 인증 ──
        composable(Routes.SPLASH) {
            SplashScreen(onNavigateToOnboarding = { navController.navigate(Routes.ONBOARDING) })
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(onNavigateToLogin = { navController.navigate(Routes.LOGIN) })
        }
        composable(Routes.LOGIN) {
            LoginScreen(onNavigateToHome = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        // ── 메인 (하단 4탭 + 마이페이지) ──
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToWorryInput    = { navController.navigate(Routes.WORRY_INPUT) },
                onNavigateToSessionDetail = { navController.navigate(Routes.SESSION_DETAIL) },
                onNavigateToSessionList   = { navController.navigate(Routes.SESSION_LIST) },
                onNavigateToRecipe        = { navController.navigate(Routes.RECIPE) },
                onNavigateToReport        = { navController.navigate(Routes.REPORT) },
                onNavigateToMyPage        = { navController.navigate(Routes.MY_PAGE) },
            )
        }
        composable(Routes.SESSION_LIST) {
            SessionListScreen(
                onNavigateToSessionDetail = { navController.navigate(Routes.SESSION_DETAIL) },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.RECIPE) {
            RecipeScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Routes.REPORT) {
            ReportScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Routes.MY_PAGE) {
            MyPageScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ── 걱정 예약 플로우 ──
        composable(Routes.WORRY_INPUT) {
            WorryInputScreen(
                onNavigateNext = { navController.navigate(Routes.WORRY_INTENSITY) },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.WORRY_INTENSITY) {
            WorryIntensityScreen(
                onNavigateNext = { navController.navigate(Routes.WORRY_SCHEDULE) },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.WORRY_SCHEDULE) {
            WorryScheduleScreen(
                onNavigateNext = { navController.navigate(Routes.WORRY_MEMO) },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.WORRY_MEMO) {
            WorryMemoScreen(
                onNavigateNext = { navController.navigate(Routes.WORRY_SAVED) },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.WORRY_SAVED) {
            WorrySavedScreen(
                onNavigateToHome = { navController.popBackStack(Routes.HOME, inclusive = false) },
            )
        }

        // ── 마음 세션 플로우 ──
        composable(Routes.SESSION_DETAIL) {
            SessionDetailScreen(
                onStartSession = { navController.navigate(Routes.SESSION_ACTIVE) },
                onSkip = { navController.popBackStack(Routes.HOME, inclusive = false) },
            )
        }
        composable(Routes.SESSION_ACTIVE) {
            SessionActiveScreen(
                onNavigateNext = { navController.navigate(Routes.SESSION_COMPLETE) },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.SESSION_COMPLETE) {
            SessionCompleteScreen(
                onNavigateNext = { navController.navigate(Routes.SESSION_RATING) },
            )
        }
        composable(Routes.SESSION_RATING) {
            SessionRatingScreen(
                onSave = { navController.popBackStack(Routes.HOME, inclusive = false) },
            )
        }
    }
}
