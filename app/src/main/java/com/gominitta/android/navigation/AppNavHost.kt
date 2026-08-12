package com.gominitta.android.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gominitta.android.presentation.SessionViewModel
import com.gominitta.android.presentation.main.MainScreen
import com.gominitta.android.presentation.mypage.MyPageScreen
import com.gominitta.android.presentation.onboarding.LoginCompleteScreen
import com.gominitta.android.presentation.onboarding.LoginScreen
import com.gominitta.android.presentation.onboarding.OnboardingScreen
import com.gominitta.android.presentation.session.SessionActiveScreen
import com.gominitta.android.presentation.session.SessionCompleteScreen
import com.gominitta.android.presentation.session.SessionDetailScreen
import com.gominitta.android.presentation.session.SessionEditScreen
import com.gominitta.android.presentation.session.SessionRatingScreen
import com.gominitta.android.presentation.session.SessionResultScreen
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
import com.gominitta.android.presentation.worry.WorryReservationViewModel
import java.time.LocalDateTime

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
    val sessionViewModel: SessionViewModel = hiltViewModel()
    LaunchedEffect(Unit) {
        sessionViewModel.sessionExpired.collect {
            navController.navigate(Routes.LOGIN) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        // 화면 전환 시 배경 투명 화면들이 겹쳐 보이는 "잔상" 방지용 짧은 크로스페이드.
        enterTransition = { fadeIn(tween(150)) },
        exitTransition = { fadeOut(tween(150)) },
        popEnterTransition = { fadeIn(tween(150)) },
        popExitTransition = { fadeOut(tween(150)) },
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
            // 로그인→홈 진입 시에만 페이드. 걱정예약/세션 플로우에서 뒤로가기로 돌아올 땐 즉시 전환.
            popEnterTransition = { EnterTransition.None },
        ) { backStackEntry ->
            val startTab = remember(backStackEntry) {
                backStackEntry.savedStateHandle.remove<String>("startTab")
            }
            MainScreen(
                startTab = startTab ?: Routes.HOME,
                onNavigateBackToSession = { navController.popBackStack() },
                onNavigateToWorryInput = { navController.navigate(Routes.WORRY_INPUT) },
                onNavigateToSessionDetail = { sessionId -> navController.navigate(Routes.sessionActiveRoute(sessionId)) },
                onNavigateToSessionEdit = { worryId -> navController.navigate(Routes.sessionEditRoute(worryId)) },
                onNavigateToSessionResult = { sessionId -> navController.navigate(Routes.sessionResultRoute(sessionId)) },
                onNavigateToWorryMemo = { sessionId -> navController.navigate(Routes.worryMemoRoute(sessionId)) },
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
                onLoggedOut = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.MY_PAGE_FAVORITE_TIME) {
            FavoriteTimeRoute(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate(Routes.MY_PAGE_FAVORITE_TIME_ADD) },
            )
        }

        composable(Routes.MY_PAGE_FAVORITE_TIME_ADD) {
            val viewModel: FavoriteTimeViewModel = hiltViewModel(
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
                onWithdrawn = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        // ── 걱정 예약 플로우 (전체화면, 바텀바 없음) ──
        // WORRY_INPUT ~ WORRY_SAVED 4개 화면이 WorryReservationViewModel 하나를 공유한다.
        composable(Routes.WORRY_INPUT) {
            val vm: WorryReservationViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsStateWithLifecycle()
            WorryInputScreen(
                title = uiState.title,
                content = uiState.content,
                onTitleChange = vm::onTitleChange,
                onContentChange = vm::onContentChange,
                onNext = { navController.navigate(Routes.WORRY_INTENSITY) },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.WORRY_INTENSITY) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.WORRY_INPUT)
            }
            val vm: WorryReservationViewModel = hiltViewModel(parentEntry)
            val uiState by vm.uiState.collectAsStateWithLifecycle()
            WorryIntensityScreen(
                intensity = uiState.intensity,
                onIntensityChange = vm::onIntensityChange,
                onNext = { navController.navigate(Routes.WORRY_SCHEDULE) },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.WORRY_SCHEDULE) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.WORRY_INPUT)
            }
            val vm: WorryReservationViewModel = hiltViewModel(parentEntry)
            val uiState by vm.uiState.collectAsStateWithLifecycle()
            WorryScheduleScreen(
                startTime = uiState.startTime,
                endTime = uiState.endTime,
                saveState = uiState.saveState,
                favoriteTimes = uiState.favoriteTimes,
                onScheduleChange = vm::onScheduleChange,
                onSubmit = { vm.save() },
                onSaved = { navController.navigate(Routes.WORRY_SAVED) },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.WORRY_MEMO,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType }),
        ) {
            WorryMemoScreen(
                viewModel = hiltViewModel(),
                onNavigateNext = { navController.popBackStack(Routes.MAIN, inclusive = false) },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.WORRY_SAVED) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.WORRY_INPUT)
            }
            val vm: WorryReservationViewModel = hiltViewModel(parentEntry)
            val uiState by vm.uiState.collectAsStateWithLifecycle()
            WorrySavedScreen(
                startTime = uiState.startTime ?: LocalDateTime.now(),
                endTime = uiState.endTime ?: LocalDateTime.now(),
                onNavigateToHome = { navController.popBackStack(Routes.MAIN, inclusive = false) },
            )
        }

        // ── 마음 세션 플로우 (전체화면, 바텀바 없음) ──
        composable(
            route = Routes.SESSION_ACTIVE,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType }),
        ) {
            SessionActiveScreen(
                onNavigateNext = { navController.navigate(Routes.SESSION_DETAIL) },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRecipeCenter = {
                    // SESSION_ACTIVE를 스택에 남겨둬야 레시피 센터에서 뒤로가기로 돌아올 수 있다.
                    navController.navigate(Routes.MAIN)
                    navController.currentBackStackEntry
                        ?.savedStateHandle?.set("startTab", Routes.RECIPE)
                },
            )
        }
        composable(Routes.SESSION_DETAIL) {
            SessionDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onSave = { navController.navigate(Routes.SESSION_COMPLETE) },
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
        composable(
            route = Routes.SESSION_EDIT,
            arguments = listOf(navArgument("worryId") { type = NavType.LongType }),
        ) {
            SessionEditScreen(
                onNavigateBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
                onDelete = { navController.popBackStack(Routes.MAIN, inclusive = false) },
            )
        }
        composable(
            route = Routes.SESSION_RESULT,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType }),
        ) {
            SessionResultScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
