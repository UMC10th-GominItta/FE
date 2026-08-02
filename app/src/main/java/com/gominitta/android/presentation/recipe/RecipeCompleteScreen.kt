package com.gominitta.android.presentation.recipe

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gominitta.android.R
import com.gominitta.android.presentation.recipe.components.RecipePrimaryButton
import com.gominitta.android.ui.theme.Gray600
import com.gominitta.android.ui.theme.Gray800

@Composable
fun RecipeCompleteScreen(
    summary: RecipeCompletionSummary,
    onFinishClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_recipe_check),
            contentDescription = null,
            modifier = Modifier
                .width(45.dp)
                .height(50.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "레시피 완료!",
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.4).sp,
            color = Gray800,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "마음이 좀 편안해지셨나요?\n오늘도 마음을 돌봐줘서 고마워요.",
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.32).sp,
            textAlign = TextAlign.Center,
            color = Gray600,
        )

        Spacer(modifier = Modifier.height(51.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFFEFFFB),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 34.dp, horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                CompletionStatColumn(
                    label = "오늘 완료한 레시피",
                    value = "${summary.todayCompletedCount}개",
                    modifier = Modifier.weight(1f),
                )

                CompletionDivider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(69.dp),
                )

                CompletionStatColumn(
                    label = "총 누적 실천",
                    value = "${summary.totalCompletedCount}번",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        RecipePrimaryButton(
            text = "완료하기",
            onClick = onFinishClick,
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}

@Composable
private fun CompletionStatColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.32).sp,
            color = Gray800,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = value,
            fontSize = 22.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.44).sp,
            color = Gray800,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * 오늘 완료한 레시피 / 총 누적 실천 사이 세로 점선.
 * border: 0.5px solid #A3A3A3, dash 2 2
 */
@Composable
private fun CompletionDivider(
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier.fillMaxHeight(),
    ) {
        drawLine(
            color = Color(0xFFA3A3A3),
            start = Offset(x = size.width / 2f, y = 0f),
            end = Offset(x = size.width / 2f, y = size.height),
            strokeWidth = 0.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(2.dp.toPx(), 2.dp.toPx()),
                phase = 0f,
            ),
        )
    }
}