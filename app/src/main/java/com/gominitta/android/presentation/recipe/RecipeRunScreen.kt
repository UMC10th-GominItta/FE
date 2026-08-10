package com.gominitta.android.presentation.recipe

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gominitta.android.R
import com.gominitta.android.presentation.recipe.components.RecipeScreenScaffold
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Button1_15m
import com.gominitta.android.ui.theme.Heading4_18m
import kotlinx.coroutines.delay
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.TextStyle
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Heading2_22m

@Composable
fun RecipeRunScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onFinishClick: () -> Unit = {},
    viewModel: RecipeRunViewModel = hiltViewModel(),
) {
    val recipe = viewModel.recipe
    var showQuitConfirmDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(viewModel.isFinished) {
        if (viewModel.isFinished) onFinishClick()
    }

    // 그만두기 버튼과 상단 뒤로가기 버튼(+ 시스템 뒤로가기)이 동일하게 동작해야 함:
    // - 모달이 이미 떠 있으면 "이어서 하기"와 동일하게 모달만 닫고 Paused 상태 유지
    // - Running 중이면 타이머를 pause시킨 뒤 모달 노출
    // - Paused 상태면 바로 모달 노출
    // - Ready/Completed면 그냥 화면 이탈
    val handleBackPress: () -> Unit = {
        when {
            showQuitConfirmDialog -> {
                showQuitConfirmDialog = false
            }
            viewModel.runStatus == RecipeRunStatus.Running -> {
                viewModel.pauseRun()
                showQuitConfirmDialog = true
            }
            viewModel.runStatus == RecipeRunStatus.Paused -> {
                showQuitConfirmDialog = true
            }
            else -> onNavigateBack()
        }
    }

    BackHandler(onBack = handleBackPress)

    RecipeScreenScaffold(
        title = "레시피 실행",
        onNavigateBack = handleBackPress,
        modifier = modifier,
    ) { innerPadding ->
        if (recipe == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return@RecipeScreenScaffold
        }

        val totalSeconds = recipe.durationMinutes * 60
        var remainingSeconds by rememberSaveable(recipe.id) {
            mutableIntStateOf(totalSeconds)
        }

        // Running일 때만 카운트다운 진행. Paused로 바뀌면 key 변경으로 이 LaunchedEffect가
        // 자동 취소되고, 다시 Running이 되면 remainingSeconds가 유지된 채로 재시작됨.
        LaunchedEffect(viewModel.runStatus) {
            if (viewModel.runStatus == RecipeRunStatus.Running) {
                while (remainingSeconds > 0) {
                    delay(1000L)
                    remainingSeconds -= 1
                }
                if (remainingSeconds <= 0) {
                    viewModel.onTimerFinished()
                }
            }
        }

        // Modifier.blur()는 API 31(Android 12) 이상에서만 실제 블러 렌더링을 지원함.
        // 그 미만 기기에서는 반투명 스크림으로 폴백 처리.
        val canBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .then(
                        if (showQuitConfirmDialog && canBlur) {
                            // 타이머 숫자가 흐릿하게라도 읽히는 정도로 강도를 낮춤.
                            Modifier.blur(8.dp)
                        } else {
                            Modifier
                        }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // topbar - 카드 간격 20dp
                Spacer(modifier = Modifier.height(20.dp))

                RecipeRunInfoCard(recipe = recipe, modifier = Modifier.fillMaxWidth())

                // 카드와 버튼 영역 사이에서 타이머가 정확히 중앙에 오도록 위아래를 동일한 weight로 분배
                Spacer(modifier = Modifier.weight(1f))

                when (viewModel.runStatus) {
                    RecipeRunStatus.Ready -> {
                        RecipeRunTimerCircle(
                            mainText = formatSeconds(totalSeconds),
                            progress = 0f,
                            dividerGap = 16.dp,
                            belowContent = {
                                Text(
                                    text = "준비되면 시작해요",
                                    style = Body3_14r,
                                    color = Color(0xFF404040),
                                    textAlign = TextAlign.Center,
                                )
                            },
                        )
                    }
                    RecipeRunStatus.Running -> {
                        val progress = if (totalSeconds == 0) 1f else
                            (totalSeconds - remainingSeconds).toFloat() / totalSeconds.toFloat()
                        RecipeRunTimerCircle(
                            mainText = formatSeconds(remainingSeconds),
                            progress = progress,
                            dividerGap = 4.dp,
                            belowContent = {
                                // 실행 중 = run 아이콘, 클릭하면 일시정지
                                Image(
                                    painter = painterResource(id = R.drawable.ic_recipe_run),
                                    contentDescription = "일시정지",
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clickable { viewModel.pauseRun() },
                                )
                            },
                        )
                    }
                    RecipeRunStatus.Paused -> {
                        val progress = if (totalSeconds == 0) 1f else
                            (totalSeconds - remainingSeconds).toFloat() / totalSeconds.toFloat()
                        RecipeRunTimerCircle(
                            mainText = formatSeconds(remainingSeconds),
                            progress = progress,
                            dividerGap = 4.dp,
                            belowContent = {
                                // 일시정지 중 = pause 아이콘, 클릭하면 재개
                                Image(
                                    painter = painterResource(id = R.drawable.ic_recipe_pause),
                                    contentDescription = "재개",
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clickable { viewModel.resumeRun() },
                                )
                            },
                        )
                    }
                    RecipeRunStatus.Completed -> {
                        RecipeRunTimerCircle(mainText = "완료", progress = 1f)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                when (viewModel.runStatus) {
                    RecipeRunStatus.Ready -> {
                        RecipeRunPrimaryButton(
                            text = "시작하기",
                            enabled = true,
                            onClick = {
                                remainingSeconds = totalSeconds
                                viewModel.startRun()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 44.dp),
                        )
                    }
                    RecipeRunStatus.Running, RecipeRunStatus.Paused -> {
                        // 그만두기(112) + 간격(8) + 완료하기(214) + 왼쪽 여백(1) = 335 = 컨테이너 폭
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 44.dp),
                        ) {
                            Spacer(modifier = Modifier.width(1.dp))
                            RecipeRunSecondaryButton(
                                text = "그만두기",
                                onClick = {
                                    if (viewModel.runStatus == RecipeRunStatus.Running) {
                                        viewModel.pauseRun()
                                    }
                                    showQuitConfirmDialog = true
                                },
                                modifier = Modifier.width(112.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            RecipeRunPrimaryButton(
                                text = "완료하기",
                                // 타이머가 다 돌아가기 전까지는 완료 불가
                                enabled = false,
                                onClick = {},
                                modifier = Modifier.width(254.dp),
                            )
                        }
                    }
                    RecipeRunStatus.Completed -> {
                        RecipeRunPrimaryButton(
                            text = "완료하기",
                            enabled = true,
                            onClick = viewModel::onFinishClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 44.dp),
                        )
                    }
                }
            }

            // 전체 화면 터치 차단(모달 아래 타이머/버튼이 눌리지 않도록) + API 31 미만 폴백 스크림
            if (showQuitConfirmDialog) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (!canBlur) {
                                Modifier.background(Color(0xFFFEFEFB).copy(alpha = 0.6f))
                            } else {
                                Modifier
                            }
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        ),
                )

                RecipeQuitConfirmDialog(
                    onDismiss = { showQuitConfirmDialog = false },
                    onConfirm = {
                        showQuitConfirmDialog = false
                        onNavigateBack()
                    },
                )
            }
        }
    }
}

@Composable
private fun RecipeRunInfoCard(recipe: RecipeItem, modifier: Modifier = Modifier) {
    Surface(
        // "오감 그라운딩 실행하기"(제목 1줄 + 설명 4줄) 기준으로 계산한 고정 높이.
        // 이 카드 높이가 고정이라야 모든 레시피에서 타이머 위치가 항상 동일함.
        modifier = modifier.height(297.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFFEFFFB),
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            RecipeDurationBadge(text = "${recipe.durationMinutes}분 소요")
            Text(
                text = recipe.title,
                style = Heading2_22m,
                textAlign = TextAlign.Center,
                color = Color.Black,
            )
            DenseDashedDivider(modifier = Modifier.widthIn(max = 202.dp))

            // 이 297dp 고정 높이는 "오감 그라운딩" 예시(제목1줄+설명4줄) 기준이라
            // 평소엔 스크롤이 필요 없음. 혹시 더 긴 설명이 들어와도 잘리지 않도록
            // 안전장치로만 스크롤을 남겨둠.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = recipe.description,
                    style = Body2_15r,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                )
            }
        }
    }
}

@Composable
private fun RecipeDurationBadge(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFFFBEACB))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-0.3).sp,
            color = Color(0xFF534B42),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DenseDashedDivider(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(1.dp)) {
        drawLine(
            color = Color(0xFFA3A3A3),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 0.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 2.dp.toPx()), 0f),
        )
    }
}

@Composable
private fun RecipeRunTimerCircle(
    mainText: String,
    progress: Float,
    modifier: Modifier = Modifier,
    dividerGap: Dp = 0.dp,
    belowContent: (@Composable () -> Unit)? = null,
) {
    Box(modifier = modifier.size(207.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFD0C1AB),
            trackColor = Color(0xFFECDFCE),
            strokeWidth = 18.dp,
            strokeCap = StrokeCap.Butt,
            gapSize = 0.dp,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = mainText,
                fontSize = if (mainText == "완료") 42.sp else 44.sp,
                lineHeight = 62.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-0.88).sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
            )
            // 타이머 숫자 아래 점선 구분선. Ready면 16dp 간격, Running/Paused면 4dp 간격
            if (belowContent != null) {
                Box(
                    modifier = Modifier
                        .width(131.dp)
                        .height(dividerGap),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                        drawLine(
                            color = Color(0xFFA3A3A3),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 0.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(2.dp.toPx(), 2.dp.toPx()),
                                0f,
                            ),
                        )
                    }
                }
                belowContent()
            }
        }
    }
}

@Composable
private fun RecipeRunPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFBEACB),
            contentColor = Color(0xFF404040),
            disabledContainerColor = Color(0xFFD4D4D4),
            disabledContentColor = Color(0xFFA6A6A6),
        ),
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.32).sp,
            color = if (enabled) Color(0xFF404040) else Color(0xFFA6A6A6),
        )
    }
}

@Composable
private fun RecipeRunSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFF9E0BA)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFEFEFB).copy(alpha = 0.5f),
            contentColor = Color(0xFF404040),
        ),
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.32).sp,
            color = Color(0xFF404040),
        )
    }
}

@Composable
private fun RecipeQuitConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                // 양옆 정확히 중앙 정렬, 하단에서 378.5dp
                .align(Alignment.BottomCenter)
                .padding(bottom = 378.5.dp)
                .size(width = 270.dp, height = 134.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(14.dp),
                    ambientColor = Color(0x33000000),
                    spotColor = Color(0x33000000),
                ),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF8F8F8),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "레시피를 그만두고 나갈까요?",
                        style = Heading4_18m,
                        color = Color(0xFF404040),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "지금 종료하시면 완료로 기록되지 않아요.",
                        style = Body3_14r,
                        color = Color(0xFF8C8C8C),
                        textAlign = TextAlign.Center,
                    )
                }

                // 버튼 영역 상단 구분선 — 세로선(0.33dp)과 동일한 두께로 통일
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.33.dp)
                        .background(Color(0xFF808080)),
                )

                Row(modifier = Modifier.fillMaxWidth().height(44.dp)) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    ) {
                        Text(text = "이어서 하기", style = Button1_15m, color = Color(0xFF404040))
                    }
                    Box(
                        modifier = Modifier
                            .width(0.33.dp)
                            .height(44.dp)
                            .background(Color(0xFF808080)),
                    )
                    TextButton(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    ) {
                        Text(text = "종료", style = Button1_15m, color = Color(0xFF404040))
                    }
                }
            }
        }
    }
}

private fun formatSeconds(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%d:%02d".format(minutes, remainingSeconds)
}