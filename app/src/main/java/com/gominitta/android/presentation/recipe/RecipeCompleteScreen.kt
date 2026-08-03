package com.gominitta.android.presentation.recipe

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import com.gominitta.android.ui.theme.Gray400
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
        Spacer(modifier = Modifier.height(170.dp))

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

        Spacer(modifier = Modifier.height(10.dp))

        TitleDashedDivider(
            modifier = Modifier.widthIn(max = 202.dp),
        )

        Spacer(modifier = Modifier.height(14.dp))

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

        RecipeCompletionStatsBox(summary = summary) // 변경 — 절대 위치 기반 335*137 상자로 교체

        Spacer(modifier = Modifier.weight(1f))

        RecipePrimaryButton(
            text = "완료하기",
            onClick = onFinishClick,
            modifier = Modifier.padding(bottom = 44.dp),
        )
    }
}

/**
 * D102-2 완료 통계 상자.
 * 335 x 137, 절대 좌표 기준 배치 (Figma 스펙 그대로):
 * - 왼쪽 라벨: x 39.5 / y 34, width 116
 * - 왼쪽 값: 라벨 밑 16dp, 116 x 31
 * - 점선: x 187.5, 상하 10.75씩 떨어짐 (세로 115.5)
 * - 오른쪽 라벨: x 219.5 / y 34, width 76 (= 335 - 219.5 - 39.5)
 * - 오른쪽 값: 라벨 밑 16dp, 76 x 31
 */
@Composable
private fun RecipeCompletionStatsBox(
    summary: RecipeCompletionSummary,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .width(335.dp)
            .height(137.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFEFFFB),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 왼쪽 — 오늘 완료한 레시피
            Text(
                text = "오늘 완료한 레시피",
                modifier = Modifier
                    .offset(x = 39.5.dp, y = 34.dp)
                    .width(116.dp),
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.32).sp,
                color = Gray800,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "${summary.todayCompletedCount}개",
                modifier = Modifier
                    .offset(x = 39.5.dp, y = 34.dp + 22.dp + 16.dp)
                    .width(116.dp)
                    .height(31.dp),
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.44).sp,
                color = Gray800,
                textAlign = TextAlign.Center,
            )

            // 점선
            CompletionDivider(
                modifier = Modifier
                    .offset(x = 187.5.dp, y = 10.75.dp)
                    .width(1.dp)
                    .height(115.5.dp),
            )

            // 오른쪽 — 총 누적 실천
            Text(
                text = "총 누적 실천",
                modifier = Modifier
                    .offset(x = 219.5.dp, y = 34.dp)
                    .width(76.dp),
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.32).sp,
                color = Gray800,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "${summary.totalCompletedCount}번",
                modifier = Modifier
                    .offset(x = 219.5.dp, y = 34.dp + 22.dp + 16.dp)
                    .width(76.dp)
                    .height(31.dp),
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.44).sp,
                color = Gray800,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * 오늘 완료한 레시피 / 총 누적 실천 사이 세로 점선.
 * border: 0.5px solid Gray400(#A6A6A6), dashed 2 2
 */
@Composable
private fun CompletionDivider(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawLine(
            color = Gray400,
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

/**
 * "레시피 완료!" 타이틀 밑 가로 점선.
 */
@Composable
private fun TitleDashedDivider(
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp),
    ) {
        drawLine(
            color = Color(0xFFA3A3A3),
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = size.width, y = 0f),
            strokeWidth = 0.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(2.dp.toPx(), 2.dp.toPx()),
                phase = 0f,
            ),
        )
    }
}