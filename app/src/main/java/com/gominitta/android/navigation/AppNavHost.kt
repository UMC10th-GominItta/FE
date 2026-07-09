package com.gominitta.android.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gominitta.android.presentation.main.MainScreen
import com.gominitta.android.presentation.mypage.MyPageScreen
import com.gominitta.android.presentation.onboarding.LoginCompleteScreen
import com.gominitta.android.presentation.onboarding.LoginScreen
import com.gominitta.android.presentation.onboarding.OnboardingScreen
import com.gominitta.android.presentation.onboarding.SplashScreen
import com.gominitta.android.presentation.session.SessionActiveScreen
import com.gominitta.android.presentation.session.SessionCompleteScreen
import com.gominitta.android.presentation.session.SessionDetailScreen
import com.gominitta.android.presentation.session.SessionRatingScreen
import com.gominitta.android.presentation.worry.WorryInputScreen
import com.gominitta.android.presentation.worry.WorryIntensityScreen
import com.gominitta.android.presentation.worry.WorryMemoScreen
import com.gominitta.android.presentation.worry.WorryScheduleScreen
import com.gominitta.android.presentation.worry.WorrySavedScreen

/**
 * Root navigation graph — the ONLY place holding the top-level [NavHostController].
 * Full-screen flows live here; the 4 bottom-tab screens live in a nested NavHost
 * inside [MainScreen] (route [Routes.MAIN]).
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
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        // ── 온보딩 · 인증 ──
        composable(Routes.SPLASH) {
            SplashScreen(onNavigateToOnboarding = { navController.navigate(Routes.ONBOARDING) })
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(onNavigateToLogin = { navController.navigate(Routes.LOGIN) })
        }
        composable(Routes.LOGIN) {
            LoginScreen(onLoginComplete = { navController.navigate(Routes.LOGIN_COMPLETE) })
        }
        composable(
            Routes.LOGIN_COMPLETE,
            exitTransition = { fadeOut(animationSpec = tween(800)) },
        ) {
            LoginCompleteScreen(onNavigateToHome = {
                navController.navigate(Routes.MAIN) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        // ── 메인 (하단 4탭 컨테이너) ──
        composable(
            Routes.MAIN,
            enterTransition = { fadeIn(animationSpec = tween(800)) },
        ) {
            MainScreen(
                onNavigateToWorryInput = { navController.navigate(Routes.WORRY_INPUT) },
                onNavigateToSessionDetail = { navController.navigate(Routes.SESSION_DETAIL) },
                onNavigateToMyPage = { navController.navigate(Routes.MY_PAGE) },
            )
        }
        composable(Routes.MY_PAGE) {
            MyPageScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ── 걱정 예약 플로우 (전체화면, 바텀바 없음) ──
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
                onNavigateToHome = { navController.popBackStack(Routes.MAIN, inclusive = false) },
            )
        }

        // ── 마음 세션 플로우 (전체화면, 바텀바 없음) ──
        composable(Routes.SESSION_DETAIL) {
            SessionDetailScreen(
                onStartSession = { navController.navigate(Routes.SESSION_ACTIVE) },
                onSkip = { navController.popBackStack(Routes.MAIN, inclusive = false) },
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
                onSave = { navController.popBackStack(Routes.MAIN, inclusive = false) },
            )
        }
    }
}
