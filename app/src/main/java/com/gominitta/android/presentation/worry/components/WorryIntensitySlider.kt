package com.gominitta.android.presentation.worry.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.theme.AccentCream100
import com.gominitta.android.ui.theme.AccentCream300
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Primary400
import com.gominitta.android.ui.theme.White800
import kotlin.math.roundToInt

/** 불안도(1~10) 선택용 커스텀 슬라이더 — 캡슐형 트랙 + 값 라벨을 보여주는 원형 썸. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorryIntensitySlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = if (value == 7) "높음" else value.toString()

    Slider(
        value = value.toFloat(),
        onValueChange = { onValueChange(it.roundToInt()) },
        modifier = modifier,
        valueRange = 1f..10f,
        steps = 8,
        thumb = {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(AccentCream300),
                contentAlignment = Alignment.Center,
            ) {
                Text(label, style = Body3_14r, color = Gray800)
            }
        },
        track = { state ->
            val capsule = RoundedCornerShape(percent = 50)
            val fraction = ((state.value - state.valueRange.start) / (state.valueRange.endInclusive - state.valueRange.start))
                .coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .clip(capsule),
            ) {
                Box(Modifier.matchParentSize().background(White800.copy(alpha = 0.5f)))
                Box(Modifier.fillMaxHeight().fillMaxWidth(fraction).background(AccentCream100))
                Box(Modifier.matchParentSize().border(1.dp, Primary400, capsule))
            }
        },
    )
}
