package com.gominitta.android.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.gominitta.android.R
import com.gominitta.android.presentation.mypage.model.ProfileImages
import com.gominitta.android.presentation.mypage.toProfileIndex
import com.gominitta.android.ui.components.GominittaButton
import com.gominitta.android.ui.components.GominittaButtonDefaults
import com.gominitta.android.ui.components.GominittaButtonVariant
import com.gominitta.android.ui.components.GominittaElevatedCard
import com.gominitta.android.ui.theme.AccentCream100
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Heading1_24sb
import com.gominitta.android.ui.theme.Heading4_18m
import com.gominitta.android.ui.theme.Primary300
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb
import com.gominitta.android.ui.theme.White800
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val HomeDateFormatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN)
private val HomeTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)

private fun formatNextSession(dateTime: LocalDateTime): String {
    val date = dateTime.format(HomeDateFormatter)
    val weekday = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)
    val time = dateTime.format(HomeTimeFormatter)
    return "$date $weekday · $time"
}

/**
 * 홈 화면 — 하단 4탭 중 첫 탭. 헤더 + 히어로 카드(걱정 예약) + 오늘의 한 마디 + 다음 마음 세션.
 * 하단 탭 바는 MainScreen 이 제공하므로 여기선 스크롤 콘텐츠만 그린다.
 *
 * 현재 표시 데이터(이름/문구/세션)는 플레이스홀더 — 추후 HomeViewModel + UseCase 로 연결.
 * "전체 보기"는 마음 세션 탭으로 전환([onNavigateToSessionList]).
 */
@Composable
fun HomeScreen(
    onNavigateToWorryInput: () -> Unit = {},
    onNavigateToWorryMemo: () -> Unit = {},
    onNavigateToSessionDetail: () -> Unit = {},
    onNavigateToSessionList: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {}
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.load()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // ① 헤더
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${uiState.nickname.ifBlank { "OO" }}님, 반가워요!",
                style = Heading1_24sb,
                color = Primary800,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Primary300)
                    .clickable(onClick = onNavigateToMyPage),
                contentAlignment = Alignment.Center,
            ) {
                if (uiState.profileImageUrl.isNotBlank()) {
                    Image(
                        painter = painterResource(ProfileImages.all[uiState.profileImageUrl.toProfileIndex()]),
                        contentDescription = "마이페이지",
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_profile),
                        contentDescription = "마이페이지",
                        modifier = Modifier.size(24.dp),
                        tint = White800,
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))

        // ② 히어로 카드 — 걱정 예약
        GominittaElevatedCard(
            contentPadding = PaddingValues(24.dp),
            decoration = {
                Image(
                    painter = painterResource(R.drawable.home_hero_leaf),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 22.dp, y = (-22).dp)
                        .requiredSize(width = 146.25.dp, height = 137.15.dp),
                )
                Image(
                    painter = painterResource(R.drawable.home_hero_acorn),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-14).dp, y = 84.dp)
                        .requiredSize(width = 86.4.dp, height = 63.dp),
                )
            },
        ) {
            Text(
                text = "걱정은 잠시 접어두고\n이따가 마주해요.",
                style = Title1_20sb,
                color = Primary800,
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "오늘은 어떤 생각이 드나요?\n약속된 시간까지 잘 보관해둘게요.",
                style = Body2_15r,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(20.dp))
            GominittaButton(
                text = "걱정 예약하기",
                onClick = onNavigateToWorryInput,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                leadingIcon = {
                    Icon(painterResource(R.drawable.ic_plus), null, Modifier.size(20.dp))
                },
                contentPadding = GominittaButtonDefaults.CompactContentPadding,
            )
        }
        Spacer(Modifier.height(4.dp))

        // ③ 오늘의 한 마디
        Text(
            "오늘의 한 마디",
            style = Heading4_18m,
            color = Primary800,
            modifier = Modifier.padding(start = 4.dp),
        )
        GominittaElevatedCard(
            decoration = {
                Image(
                    painter = painterResource(R.drawable.home_quote_leaf),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-12).dp, y = 0.dp)
                        .requiredSize(width = 50.3.dp, height = 45.2.dp),
                )
            },
        ) {
            Text(
                text = uiState.dailyMessage,
                style = Body1_16m,
                color = Primary800,
            )
        }
        Spacer(Modifier.height(4.dp))

        // ④ 다음 마음 세션
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("다음 마음 세션", style = Heading4_18m, color = Primary800)
            Text(
                "전체 보기",
                style = Body3_14r,
                color = Gray400,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onNavigateToSessionList),
            )
        }
        GominittaElevatedCard(
            decoration = {
                Image(
                    painter = painterResource(R.drawable.home_session_leaf),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-6).dp, y = 2.dp)
                        .requiredSize(width = 96.7.dp, height = 93.7.dp),
                )
            },
        ) {
            val nextSession = uiState.nextSession
            if (nextSession == null) {
                Text(
                    text = "예약된 세션이 없어요",
                    style = Body3_14r,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentCream100),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_calendar),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Primary800,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = formatNextSession(nextSession.startedAt),
                        style = Body3_14r,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = nextSession.title,
                    style = Body1_16m,
                    color = Primary800,
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GominittaButton(
                        text = "한 줄 보태기",
                        onClick = onNavigateToWorryMemo,
                        modifier = Modifier.weight(1f).height(44.dp),
                        variant = GominittaButtonVariant.Outlined,
                        leadingIcon = {
                            Icon(painterResource(R.drawable.ic_chat), null, Modifier.size(18.dp))
                        },
                        contentPadding = GominittaButtonDefaults.CompactContentPadding,
                    )
                    GominittaButton(
                        text = "세션 시작",
                        onClick = onNavigateToSessionDetail,
                        modifier = Modifier.weight(1f).height(44.dp),
                        leadingIcon = {
                            Icon(painterResource(R.drawable.ic_play), null, Modifier.size(18.dp))
                        },
                        contentPadding = GominittaButtonDefaults.CompactContentPadding,
                    )
                }
            }
        }
    }
}
