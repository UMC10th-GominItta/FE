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
import com.gominitta.android.presentation.session.SessionActiveScreen
import com.gominitta.android.presentation.session.SessionCompleteScreen
import com.gominitta.android.presentation.session.SessionDetailScreen
import com.gominitta.android.presentation.session.SessionRatingScreen
import com.gominitta.android.presentation.worry.WorryInputScreen
import com.gominitta.android.presentation.worry.WorryIntensityScreen
import com.gominitta.android.presentation.worry.WorryMemoScreen
import com.gominitta.android.presentation.worry.WorryScheduleScreen
import com.gominitta.android.presentation.worry.WorrySavedScreen
import com.gominitta.android.presentation.mypage.FavoriteTimeAddRoute
import com.gominitta.android.presentation.mypage.FavoriteTimeRoute
import com.gominitta.android.presentation.mypage.MyPageRoute
import com.gominitta.android.presentation.mypage.NotificationSettingRoute
import com.gominitta.android.presentation.mypage.ProfileEditRoute
import com.gominitta.android.presentation.mypage.WithdrawScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gominitta.android.presentation.mypage.model.FavoriteTimeViewModel
/**
 * Root navigation graph — the ONLY place holding the top-level [NavHostController].
 * Full-screen flows live here; the 4 bottom-tab screens live in a nested NavHost
 * inside [MainScreen] (route [Routes.MAIN]).
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.ONBOARDING,
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
        composable(Routes.ONBOARDING) {
            OnboardingScreen(onNavigateToLogin = { navController.navigate(Routes.LOGIN) })
        }
        composable(Routes.LOGIN) {
            LoginScreen(onLoginComplete = {
                // 완료 화면 진입 시 로그인/온보딩 스택을 즉시 비운다 —
                // 자동 이동 대기(1.5s) 중 뒤로가기로 로그인에 갇히는 것 방지 + 더블탭 중복 방지.
                navController.navigate(Routes.LOGIN_COMPLETE) {
                    popUpTo(Routes.ONBOARDING) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }
        composable(
            Routes.LOGIN_COMPLETE,
            exitTransition = { fadeOut(animationSpec = tween(800)) },
        ) {
            LoginCompleteScreen(onNavigateToHome = {
                navController.navigate(Routes.MAIN) {
                    popUpTo(Routes.LOGIN_COMPLETE) { inclusive = true }
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
            MyPageRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onFavoriteTimeClick = {
                    navController.navigate(
                        Routes.MY_PAGE_FAVORITE_TIME,
                    )
                },
                onNotificationSettingClick = {
                    navController.navigate(
                        Routes.MY_PAGE_NOTIFICATION,
                    )
                },
                onProfileEditClick = {
                    navController.navigate(
                        Routes.MY_PAGE_PROFILE_EDIT,
                    )
                },
                onWithdrawClick = {
                    navController.navigate(
                        Routes.MY_PAGE_WITHDRAW,
                    )
                },
                onLogoutConfirmed = {
                    // TODO 실제 로그아웃 처리 후 로그인 화면 이동
                },
            )
        }
        composable(Routes.MY_PAGE_FAVORITE_TIME) {
            val viewModel: FavoriteTimeViewModel = viewModel()   // Hilt 쓰면 hiltViewModel()
            FavoriteTimeRoute(
                favoriteTimes = viewModel.favoriteTimes,
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate(Routes.MY_PAGE_FAVORITE_TIME_ADD) },
            )
        }

        composable(Routes.MY_PAGE_FAVORITE_TIME_ADD) {
            // 같은 그래프 안이면 이전 백스택 엔트리에서 같은 ViewModel 인스턴스를 다시 얻을 수 있음
            val viewModel: FavoriteTimeViewModel = viewModel(
                navController.getBackStackEntry(Routes.MY_PAGE_FAVORITE_TIME),
            )
            FavoriteTimeAddRoute(
                onBackClick = { navController.popBackStack() },
                onSaved = {
                    viewModel.add(it)
                    navController.popBackStack()
                },
            )
        }

        composable(Routes.MY_PAGE_NOTIFICATION) {
            NotificationSettingRoute(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }

        composable(Routes.MY_PAGE_PROFILE_EDIT) {
            ProfileEditRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaved = {
                    navController.popBackStack()
                },
            )
        }

        composable(Routes.MY_PAGE_WITHDRAW) {
            WithdrawScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCancelClick = {
                    navController.popBackStack()
                },
                onWithdrawClick = {
                    // TODO 회원 탈퇴 API 성공 후 로그인 화면 이동
                },
            )
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
