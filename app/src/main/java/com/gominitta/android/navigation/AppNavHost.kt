package com.gominitta.android.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.gominitta.android.presentation.worry.WorryInputScreen
import com.gominitta.android.presentation.worry.WorryIntensityScreen
import com.gominitta.android.presentation.worry.WorryMemoScreen
import com.gominitta.android.presentation.worry.WorryScheduleScreen
import com.gominitta.android.presentation.worry.WorrySavedScreen
import com.gominitta.android.presentation.worry.WorrySaveViewModel
import com.gominitta.android.presentation.mypage.FavoriteTimeAddRoute
import com.gominitta.android.presentation.mypage.FavoriteTimeRoute
import com.gominitta.android.presentation.mypage.MyPageRoute
import com.gominitta.android.presentation.mypage.NotificationSettingRoute
import com.gominitta.android.presentation.mypage.ProfileEditRoute
import com.gominitta.android.presentation.mypage.WithdrawScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gominitta.android.presentation.mypage.model.FavoriteTimeViewModel
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
    // 걱정 예약 플로우(WORRY_INPUT~WORRY_SAVED) 단계 간에 공유하는 임시 상태 —
    // 각 화면은 여전히 콜백에 자기 값을 실어 넘기고, 여기서는 다음 단계로 전달할 값만 들고 있는다.
    var worryTitle by remember { mutableStateOf("") }
    var worryContent by remember { mutableStateOf("") }
    var worryIntensity by remember { mutableStateOf(5) }
    var worryStartTime by remember { mutableStateOf(LocalDateTime.now()) }
    var worryEndTime by remember { mutableStateOf(LocalDateTime.now()) }

    // 마음 세션 플로우(SESSION_ACTIVE → SESSION_DETAIL) 단계 간에 기록한 텍스트를 넘기는 임시 상태.
    var sessionRecordText by remember { mutableStateOf("") }

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
                onNavigateToSessionActive = { sessionId -> navController.navigate(Routes.sessionActiveRoute(sessionId)) },
                onNavigateToSessionEdit = { sessionId -> navController.navigate(Routes.sessionEditRoute(sessionId)) },
                onNavigateToWorryMemo = { navController.navigate(Routes.WORRY_MEMO) },
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
                onNavigateNext = { title, content ->
                    worryTitle = title
                    worryContent = content
                    navController.navigate(Routes.WORRY_INTENSITY)
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.WORRY_INTENSITY) {
            WorryIntensityScreen(
                onNavigateNext = { intensity ->
                    worryIntensity = intensity
                    navController.navigate(Routes.WORRY_SCHEDULE)
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.WORRY_SCHEDULE) {
            val saveViewModel: WorrySaveViewModel = hiltViewModel()
            WorryScheduleScreen(
                onNavigateNext = { startTime, endTime ->
                    worryStartTime = startTime
                    worryEndTime = endTime
                    saveViewModel.save(
                        worryContent = worryTitle,
                        worryMemo = worryContent,
                        scheduledStartAt = startTime,
                        scheduledEndAt = endTime,
                        emotionScoreBefore = worryIntensity,
                        onSaved = { navController.navigate(Routes.WORRY_SAVED) },
                    )
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.WORRY_MEMO) {
            WorryMemoScreen(
                onNavigateNext = { navController.popBackStack(Routes.MAIN, inclusive = false) },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.WORRY_SAVED) {
            WorrySavedScreen(
                startTime = worryStartTime,
                endTime = worryEndTime,
                onNavigateToHome = { navController.popBackStack(Routes.MAIN, inclusive = false) },
            )
        }

        // ── 마음 세션 플로우 (전체화면, 바텀바 없음) ──
        composable(
            route = Routes.SESSION_ACTIVE,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            SessionActiveScreen(
                sessionId = sessionId,
                onNavigateNext = { recordedText ->
                    sessionRecordText = recordedText
                    navController.navigate(Routes.sessionDetailRoute(sessionId))
                },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRecipeCenter = {
                    // SESSION_ACTIVE를 스택에 남겨둬야 레시피 센터에서 뒤로가기로 돌아올 수 있다.
                    navController.navigate(Routes.MAIN)
                    navController.currentBackStackEntry
                        ?.savedStateHandle?.set("startTab", Routes.RECIPE)
                },
            )
        }
        composable(
            route = Routes.SESSION_DETAIL,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            SessionDetailScreen(
                sessionId = sessionId,
                initialText = sessionRecordText,
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
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            SessionEditScreen(
                sessionId = sessionId,
                onNavigateBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
                onDelete = { navController.popBackStack(Routes.MAIN, inclusive = false) },
            )
        }
    }
}
