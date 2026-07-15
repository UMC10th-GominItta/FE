package com.gominitta.android.presentation.recipe

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gominitta.android.R
import kotlinx.coroutines.delay

/**
 * D102 레시피 실행 화면
 *
 * Figma상 D102 / D102-1 / D102-2를
 * 실제 구현에서는 하나의 화면에서 상태만 바꿔 처리한다.
 *
 * Ready
 * - 시작 전
 * - 타이머 안에 전체 시간 표시
 * - 시작하기 버튼 활성화
 *
 * Running
 * - 타이머 진행
 * - 완료하기 버튼 비활성화(회색)
 *
 * Completed
 * - 타이머 안에 "완료"
 * - 완료하기 버튼 활성화
 */
@Composable
fun RecipeRunScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    recipe: RecipeItem = sampleRecipes.first(),
    onFinishClick: () -> Unit = {},
) {
    val totalSeconds = recipe.durationMinutes * 60

    var runStatus by rememberSaveable {
        mutableStateOf(RecipeRunStatus.Ready)
    }

    var remainingSeconds by rememberSaveable(recipe.id) {
        mutableIntStateOf(totalSeconds)
    }

    LaunchedEffect(runStatus) {
        if (runStatus == RecipeRunStatus.Running) {
            while (remainingSeconds > 0) {
                delay(1000L)
                remainingSeconds -= 1
            }

            if (remainingSeconds <= 0) {
                runStatus = RecipeRunStatus.Completed
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RecipeRunTopBar(
                title = "레시피 실행",
                onBackClick = onNavigateBack,
            )

            Spacer(modifier = Modifier.height(12.dp))

            RecipeRunInfoCard(
                recipe = recipe,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (runStatus) {
                RecipeRunStatus.Ready -> {
                    RecipeReadyContent(
                        totalSeconds = totalSeconds,
                        onStartClick = {
                            remainingSeconds = totalSeconds
                            runStatus = RecipeRunStatus.Running
                        },
                    )
                }

                RecipeRunStatus.Running -> {
                    RecipeRunningContent(
                        remainingSeconds = remainingSeconds,
                        totalSeconds = totalSeconds,
                    )
                }

                RecipeRunStatus.Completed -> {
                    RecipeCompletedContent(
                        onFinishClick = onFinishClick,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * 상단 뒤로가기 + 타이틀 바
 */
@Composable
private fun RecipeRunTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_recipe_upback),
                contentDescription = "뒤로가기",
                modifier = Modifier.size(24.dp),
            )
        }

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.4).sp,
            textAlign = TextAlign.Center,
            color = Color(0xFF404040),
        )

        Spacer(modifier = Modifier.size(40.dp))
    }
}

/**
 * 상단 레시피 정보 카드
 * - 소요시간
 * - 레시피명
 * - 점선
 * - 설명
 */
@Composable
private fun RecipeRunInfoCard(
    recipe: RecipeItem,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(267.dp),
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
                fontSize = 22.sp,
                lineHeight = 31.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.44).sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
            )

            DenseDashedDivider(
                modifier = Modifier.widthIn(max = 202.dp),
            )

            Text(
                text = recipe.description,
                fontSize = 15.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-0.3).sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
            )
        }
    }
}

/**
 * 상단 "5분 소요" 배지
 * 텍스트: 15px Pretendard Regular
 * 색상: #534B42
 */
@Composable
private fun RecipeDurationBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
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

/**
 * Figma처럼 더 촘촘한 점선
 * - 굵기 0.5
 * - dash 2, gap 2 정도로 처리
 */
@Composable
private fun DenseDashedDivider(
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp),
    ) {
        drawLine(
            color = Color(0xFFA3A3A3),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 0.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(2.dp.toPx(), 2.dp.toPx()),
                phase = 0f,
            ),
        )
    }
}

/**
 * 시작 전
 */
@Composable
private fun RecipeReadyContent(
    totalSeconds: Int,
    onStartClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RecipeRunTimerCircle(
            mainText = formatSeconds(totalSeconds),
            subText = "준비되면 시작해요",
            progress = 0f,
        )

        Spacer(modifier = Modifier.height(20.dp))

        RecipeRunPrimaryButton(
            text = "시작하기",
            onClick = onStartClick,
            enabled = true,
        )
    }
}

/**
 * 실행 중
 * - 완료하기 버튼은 비활성화
 * - 타이머가 0초가 되면 Completed 상태로 전환
 */
@Composable
private fun RecipeRunningContent(
    remainingSeconds: Int,
    totalSeconds: Int,
) {
    val progress = if (totalSeconds == 0) {
        1f
    } else {
        (totalSeconds - remainingSeconds).toFloat() / totalSeconds.toFloat()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RecipeRunTimerCircle(
            mainText = formatSeconds(remainingSeconds),
            subText = "",
            progress = progress,
        )

        Spacer(modifier = Modifier.height(20.dp))

        RecipeRunPrimaryButton(
            text = "완료하기",
            onClick = {},
            enabled = false,
        )
    }
}

/**
 * 완료 상태
 * - 완료하기 버튼 활성화
 */
@Composable
private fun RecipeCompletedContent(
    onFinishClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RecipeRunTimerCircle(
            mainText = "완료",
            subText = "",
            progress = 1f,
        )

        Spacer(modifier = Modifier.height(20.dp))

        RecipeRunPrimaryButton(
            text = "완료하기",
            onClick = onFinishClick,
            enabled = true,
        )
    }
}

/**
 * 원형 타이머
 *
 * 배경 트랙: #ECDFCE
 * 진행색: #D0C1AB
 *
 * 타이머 숫자: 44px Pretendard Regular
 * 준비되면 시작해요: 15px Pretendard Regular, #404040
 */
@Composable
private fun RecipeRunTimerCircle(
    mainText: String,
    subText: String,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(207.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFD0C1AB),
            trackColor = Color(0xFFECDFCE),
            strokeWidth = 18.dp,
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = mainText,
                fontSize = if (mainText == "완료") 42.sp else 44.sp,
                lineHeight = 62.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-0.88).sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
            )

            if (subText.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subText,
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = (-0.3).sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF404040),
                )
            }
        }
    }
}

/**
 * 하단 버튼
 *
 * 시작하기 / 완료하기
 * - 텍스트: 16px Pretendard Medium
 * - 색상: #404040
 */
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
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFBEACB),
            contentColor = Color(0xFF404040),
            disabledContainerColor = Color(0xFFD9D9D9),
            disabledContentColor = Color(0xFFA6A6A6),
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

/**
 * 초 -> m:ss 형식
 */
private fun formatSeconds(seconds: Int): String {
    val minutes = seconds / 60
    val remainSeconds = seconds % 60
    return "%d:%02d".format(minutes, remainSeconds)
}