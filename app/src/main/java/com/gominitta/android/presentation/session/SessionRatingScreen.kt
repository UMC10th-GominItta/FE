package com.gominitta.android.presentation.session

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.ui.components.GominittaButton
import com.gominitta.android.ui.theme.AccentCream100
import com.gominitta.android.ui.theme.AccentCream300
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Heading1_24sb
import com.gominitta.android.ui.theme.Heading3_20m
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Title1_20sb
import kotlin.math.roundToInt

/**
 * 마음 세션 평가 (C104-2) — 세션 완료 → 저장. 0~10 감정 점수를 슬라이더로 입력한다
 * (emotionScoreAfter, 서버 명세와 동일 스케일). 점수 구간(0 / 1~2 / 3~4 / 5~6 / 7~8 / 9~10)에
 * 맞는 고양이 일러스트를 [MoodIllustration] 이 보여준다.
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
                color = Gray800,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            )
            Spacer(Modifier.height(36.dp))
            Text(
                text = "걱정을 마주한 후, 지금의 기분은 어떤가요?",
                style = Heading3_20m,
                color = Gray800,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.weight(1f))

            MoodIllustration(score = emotionScore.roundToInt())

            Spacer(Modifier.height(32.dp))
            Text(text = emotionScore.toMoodLabel(), style = Heading1_24sb, color = Gray800, textAlign = TextAlign.Center)
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
        Text(text = label, style = Body3_14r, color = Gray800)
    }
}

@Composable
private fun MoodIllustration(score: Int) {
    Image(
        painter = painterResource(score.toMoodCatDrawableRes()),
        contentDescription = "감정 표현 고양이",
        modifier = Modifier.size(180.dp),
        contentScale = ContentScale.Fit,
    )
}


private fun Int.toMoodCatDrawableRes(): Int = when (coerceIn(0, 10)) {
    0 -> R.drawable.worry_cat_0
    1, 2 -> R.drawable.worry_cat_1_2
    3, 4 -> R.drawable.worry_cat_3_4
    5, 6 -> R.drawable.worry_cat_5_6
    7, 8 -> R.drawable.worry_cat_7_8
    else -> R.drawable.worry_cat_9_10
}

/** 0~10 (emotionScoreAfter 스케일) 정수 단계별 기분 멘트. */
private val MoodLabels = listOf(
    "완전히 평온해졌어요",       // 0
    "마음이 잔잔하고 편안해요",   // 1
    "이제 가볍게 넘길 수 있어요", // 2
    "마음이 조금 진정됐어요",     // 3
    "아직 잔상이 조금 남아있어요", // 4
    "여전히 미미한 불안이 있어요", // 5
    "여전히 조금 강한 불안이 느껴져요", // 6
    "여전히 많이 불안해요",       // 7
    "아직 불안해서 집중이 안 돼요", // 8
    "여전히 너무 불안하고 초조해요", // 9
    "여전히 터질 듯이 불안해요",   // 10
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
