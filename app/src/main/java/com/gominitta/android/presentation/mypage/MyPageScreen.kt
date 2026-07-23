package com.gominitta.android.presentation.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.presentation.mypage.components.MyPageOutlinedButton
import com.gominitta.android.presentation.mypage.components.MyPagePrimaryButton
import com.gominitta.android.presentation.mypage.components.MyPageProfileCard
import com.gominitta.android.presentation.mypage.components.MyPageSectionTitle
import com.gominitta.android.presentation.mypage.components.MyPageSettingRow
import com.gominitta.android.presentation.mypage.components.MyPageTopBar
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray600
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Title1_20sb
import androidx.compose.ui.platform.LocalDensity

@Composable
fun MyPageRoute(
    onBackClick: () -> Unit,
    onFavoriteTimeClick: () -> Unit,
    onNotificationSettingClick: () -> Unit,
    onProfileEditClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onLogoutConfirmed: () -> Unit,
) {
    var showLogoutSheet by rememberSaveable { mutableStateOf(false) }
    var sheetTopY by remember { mutableStateOf(0f) }

    MyPageScreen(
        nickname = "00님",
        email = "abcdef@gmail.com",
        isEditing = showLogoutSheet,
        sheetTopY = sheetTopY,
        onBackClick = onBackClick,
        onFavoriteTimeClick = onFavoriteTimeClick,
        onNotificationSettingClick = onNotificationSettingClick,
        onProfileEditClick = onProfileEditClick,
        onLogoutClick = { showLogoutSheet = true },
        onWithdrawClick = onWithdrawClick,
    )

    if (showLogoutSheet) {
        LogoutBottomSheet(
            onDismissRequest = { showLogoutSheet = false },
            onConfirmClick = {
                showLogoutSheet = false
                onLogoutConfirmed()
            },
            onSheetPositioned = { sheetTopY = it },
        )
    }
}

@Composable
fun MyPageScreen(
    nickname: String,
    email: String,
    onBackClick: () -> Unit,
    onFavoriteTimeClick: () -> Unit,
    onNotificationSettingClick: () -> Unit,
    onProfileEditClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    isEditing: Boolean = false,
    sheetTopY: Float = 0f,
) {
    val density = LocalDensity.current
    var boxBottomY by remember { mutableStateOf(0f) } // 이 Box 하단의 window y좌표

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            MyPageTopBar(title = "마이 페이지", onBackClick = onBackClick)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .onGloballyPositioned { coordinates ->
                    boxBottomY = coordinates.positionInWindow().y + coordinates.size.height
                },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                MyPageProfileCard(
                    nickname = nickname,
                    email = email,
                    onProfileEditClick = onProfileEditClick,
                )
                Spacer(modifier = Modifier.height(30.dp))
                MyPageSectionTitle(title = "개인 설정")
                Spacer(modifier = Modifier.height(10.dp))
                MyPageSettingRow(
                    iconRes = R.drawable.ic_mypage_clock,
                    title = "즐겨찾는 시간 관리",
                    onClick = onFavoriteTimeClick,
                )
                Spacer(modifier = Modifier.height(10.dp))
                MyPageSettingRow(
                    iconRes = R.drawable.ic_mypage_alarm,
                    title = "푸시 알림 설정",
                    onClick = onNotificationSettingClick,
                )
                Spacer(modifier = Modifier.height(28.dp))
                MyPageSectionTitle(title = "계정 관리")
                Spacer(modifier = Modifier.height(10.dp))
                MyPageSettingRow(
                    iconRes = R.drawable.ic_mypage_logout,
                    title = "로그아웃",
                    onClick = onLogoutClick,
                )
                Spacer(modifier = Modifier.height(10.dp))
                MyPageSettingRow(
                    iconRes = R.drawable.ic_mypage_withdraw,
                    title = "회원탈퇴",
                    onClick = onWithdrawClick,
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            if (isEditing && sheetTopY > 0f && boxBottomY > 0f) {
                // 이미지 하단이 팝업 상단(sheetTopY)에 정확히 맞닿도록,
                // Box 하단 기준으로 위로 밀어올림
                val liftPx = boxBottomY - sheetTopY
                val liftDp = with(density) { liftPx.toDp() }

                Image(
                    painter = painterResource(R.drawable.ic_mypage_logout_grad),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = -liftDp) // 팝업 상단만큼 위로 띄움 → 겹치지 않음
                        .fillMaxWidth()
                        .height(424.dp),
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogoutBottomSheet(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    onSheetPositioned: (Float) -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        scrimColor = Color.Transparent,
        shape = RoundedCornerShape(0.dp),
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary200) // #F3F0EB
                .navigationBarsPadding()
                .onGloballyPositioned { coordinates ->
                    onSheetPositioned(coordinates.positionInWindow().y)
                },
        ) {
            // 상단 검정 테두리
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Color.Black),
            )

            // 드래그 핸들
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(width = 50.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(Gray400),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 24.dp, bottom = 24.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_mypage_rightleaf),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 22.dp, y = (-70).dp)
                        .requiredSize(width = 82.dp, height = 116.dp),
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "정말 로그아웃 하시겠습니까?",
                        style = Title1_20sb,
                        color = Gray800,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "로그아웃 하셔도 기존에 저장된 데이터는\n안전하게 보관됩니다.",
                        style = Body1_16m,
                        color = Gray600,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(60.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        MyPageOutlinedButton(
                            text = "취소",
                            onClick = onDismissRequest,
                            modifier = Modifier.weight(1f),
                        )
                        MyPagePrimaryButton(
                            text = "로그아웃",
                            onClick = onConfirmClick,
                            modifier = Modifier.weight(2f),
                        )
                    }
                }

                Image(
                    painter = painterResource(R.drawable.ic_mypage_leftleaf),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = (-12).dp, y = (-20).dp)
                        .requiredSize(width = 82.dp, height = 116.dp),
                )
            }
        }
    }
}