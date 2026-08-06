package com.gominitta.android.presentation.worry

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.presentation.worry.components.WorryIntensitySlider
import com.gominitta.android.presentation.worry.components.WorryPrimaryButton
import com.gominitta.android.presentation.worry.components.WorryTopBar
import com.gominitta.android.ui.components.GominittaBackground
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading4_18m
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb

/**
 * 걱정 정도 선택 (B101-1) — 걱정 입력 → 다음. 슬라이더로 불안도(1~10)를 고른다.
 */
@Composable
fun WorryIntensityScreen(
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var intensity by remember { mutableStateOf(5) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            WorryTopBar(title = "걱정 예약하기", onBack = onNavigateBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(36.dp))

                Text(
                    text = "지금 나의 걱정은 어느 정도인가요?",
                    style = Heading4_18m,
                    color = Gray800,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(100.dp))

                Image(
                    painter = painterResource(worryCatImage(intensity)),
                    contentDescription = "불안도 고양이",
                    modifier = Modifier.size(220.dp),
                    contentScale = ContentScale.Fit,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = worryCatCaption(intensity),
                    style = Title1_20sb,
                    color = Primary800,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(52.dp))

                WorryIntensitySlider(
                    value = intensity,
                    onValueChange = { intensity = it },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            WorryPrimaryButton(
                text = "다음",
                onClick = onNavigateNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp),
                enabled = true,
            )
        }
    }
}

@DrawableRes
private fun worryCatImage(intensity: Int): Int = when (intensity) {
    0 -> R.drawable.worry_cat_0
    1, 2 -> R.drawable.worry_cat_1_2
    3, 4 -> R.drawable.worry_cat_3_4
    5, 6 -> R.drawable.worry_cat_5_6
    7, 8 -> R.drawable.worry_cat_7_8
    else -> R.drawable.worry_cat_9_10
}

private fun worryCatCaption(intensity: Int): String = when (intensity) {
    0 -> "아주 평온해요."
    1 -> "잔잔하고 편안해요."
    2 -> "살짝 신경이 쓰여요."
    3 -> "조금 싱숭생숭해요."
    4 -> "계속 신경이 쓰여요."
    5 -> "미미한 불안이 있어요."
    6 -> "조금 강한 불안이 느껴져요."
    7 -> "은근히 많이 불안해요."
    8 -> "꽤 불안해서 집중이 안 돼요."
    9 -> "너무 불안해서 초조해요."
    else -> "터질 것처럼 너무 불안해요."
}

// ---- Preview ---------------------------------------------------------------

@Preview(name = "WorryIntensity", showBackground = true, backgroundColor = 0xFFFEFEFB, widthDp = 375, heightDp = 812)
@Composable
private fun WorryIntensityScreenPreview() {
    GominittaTheme {
        GominittaBackground {
            WorryIntensityScreen(onNavigateNext = {}, onNavigateBack = {})
        }
    }
}
