package com.gominitta.android.presentation.recipe

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gominitta.android.presentation.recipe.components.RecipeScreenScaffold
import kotlinx.coroutines.delay

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

    RecipeScreenScaffold(
        title = "레시피 실행",
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(4.dp))

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
                .padding(
                    horizontal = 24.dp,
                    vertical = 36.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            RecipeDurationBadge(
                text = "${recipe.durationMinutes}분 소요",
            )

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

@Composable
private fun RecipeDurationBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFFFBEACB))
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),
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
            start = Offset(
                x = 0f,
                y = 0f,
            ),
            end = Offset(
                x = size.width,
                y = 0f,
            ),
            strokeWidth = 0.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(
                    2.dp.toPx(),
                    2.dp.toPx(),
                ),
                phase = 0f,
            ),
        )
    }
}

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

@Composable
private fun RecipeRunningContent(
    remainingSeconds: Int,
    totalSeconds: Int,
) {
    val progress =
        if (totalSeconds == 0) {
            1f
        } else {
            (totalSeconds - remainingSeconds).toFloat() /
                    totalSeconds.toFloat()
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
            progress = progress.coerceIn(
                minimumValue = 0f,
                maximumValue = 1f,
            ),
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
                fontSize =
                    if (mainText == "완료") {
                        42.sp
                    } else {
                        44.sp
                    },
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
            color =
                if (enabled) {
                    Color(0xFF404040)
                } else {
                    Color(0xFFA6A6A6)
                },
        )
    }
}

private fun formatSeconds(
    seconds: Int,
): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return "%d:%02d".format(
        minutes,
        remainingSeconds,
    )
}