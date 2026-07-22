package com.gominitta.android.presentation.worry

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
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
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading4_18m
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb

/**
 * 걱정 정도 선택 (B101-1) — 걱정 입력 → 다음. 슬라이더로 불안도(1~10)를 고른다.
 */
@Composable
fun WorryIntensityScreen(
    onNavigateNext: (intensity: Int) -> Unit,
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

                if (intensity == 7) {
                    Image(
                        painter = painterResource(R.drawable.worry_cat_anxious),
                        contentDescription = "불안한 고양이",
                        modifier = Modifier.size(220.dp),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Primary200),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("$intensity 준비중", style = Body3_14r, color = Gray400)
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = if (intensity == 7) "꽤 불안해요." else "$intensity 준비중",
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
                onClick = { onNavigateNext(intensity) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp),
                enabled = true,
            )
        }
    }
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
