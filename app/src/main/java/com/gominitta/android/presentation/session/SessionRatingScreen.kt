package com.gominitta.android.presentation.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.components.GominittaButton
import com.gominitta.android.ui.theme.AccentCream100
import com.gominitta.android.ui.theme.AccentCream300
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading1_24sb
import com.gominitta.android.ui.theme.Heading3_20m
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb
import kotlin.math.roundToInt

/**
 * 마음 세션 평가 (C104-2) — 세션 완료 → 저장. 0~10 감정 점수를 슬라이더로 입력한다
 * (emotionScoreAfter, 서버 명세와 동일 스케일). 고양이 표정 일러스트는 실제 에셋이 없어
 * 자리표시자로만 둔다 — 실제 그림 받으면 [PlaceholderMoodIllustration] 만 교체하면 됨.
 */
@Composable
fun SessionRatingScreen(
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var emotionScore by remember { mutableFloatStateOf(5f) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "마음 세션",
                style = Title1_20sb,
                color = Primary800,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "걱정을 마주한 후, 지금의 기분은 어떤가요?",
                style = Heading3_20m,
                color = Primary800,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.weight(1f))

            PlaceholderMoodIllustration()

            Spacer(Modifier.height(32.dp))
            Text(text = emotionScore.toMoodLabel(), style = Heading1_24sb, color = Primary800, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            MoodSlider(
                value = emotionScore,
                onValueChange = { emotionScore = it },
            )

            Spacer(Modifier.weight(1f))
            GominittaButton(
                text = "저장하기",
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** 손잡이 자체가 현재 값 라벨 말풍선을 겸하는 슬라이더 (Figma C104-2 "적당" 손잡이). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoodSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = 0f..10f,
        steps = 9,
        modifier = modifier.fillMaxWidth(),
        track = { MoodSliderTrack(fraction = (value / 10f).coerceIn(0f, 1f)) },
        thumb = { MoodSliderThumb(label = value.roundToInt().toString()) },
    )
}

@Composable
private fun MoodSliderTrack(fraction: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(50))
            .background(AccentCream100)
            .border(1.dp, AccentCream300, RoundedCornerShape(50)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(AccentCream300),
        )
    }
}

@Composable
private fun MoodSliderThumb(label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(AccentCream300)
            .border(1.dp, Primary200, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = Body3_14r, color = Primary800)
    }
}

@Composable
private fun PlaceholderMoodIllustration() {
    Box(
        modifier = Modifier
            .size(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Primary200),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "고양이 표정\n일러스트 준비 중",
            style = Body3_14r,
            color = Gray400,
            textAlign = TextAlign.Center,
        )
    }
}

/** 0~10 (emotionScoreAfter 스케일) 정수 단계별 기분 멘트. */
private val MoodLabels = listOf(
    "완전히 평온해졌어요.",       // 0
    "마음이 잔잔하고 편안해요.",   // 1
    "이제 가볍게 넘길 수 있어요.", // 2
    "마음이 조금 진정됐어요.",     // 3
    "아직 잔상이 조금 남아있어요.", // 4
    "여전히 미미한 불안이 있어요.", // 5
    "여전히 조금 강한 불안이 느껴져요.", // 6
    "여전히 많이 불안해요.",       // 7
    "아직 불안해서 집중이 안 돼요.", // 8
    "여전히 너무 불안하고 초조해요.", // 9
    "여전히 터질 듯이 불안해요.",   // 10
)

private fun Float.toMoodLabel(): String = MoodLabels[roundToInt().coerceIn(0, 10)]

// ---- Preview ---------------------------------------------------------------

@Preview(name = "SessionRating", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun SessionRatingScreenPreview() {
    GominittaTheme {
        SessionRatingScreen(onSave = {})
    }
}
