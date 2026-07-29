package com.gominitta.android.presentation.worry.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.theme.AccentCream100
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Primary300
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.White800

/** 즐겨찾는 시간 — 시각은 자정 기준 분(0..1440). 1440은 24:00을 뜻한다. */
data class WorryFavoriteTime(
    val label: String,
    val startMinutes: Int,
    val endMinutes: Int,
)

/** 즐겨찾는 시간 카드 — 라벨과 시작~종료 시각을 보여주고 탭하면 선택된다. */
@Composable
fun WorryFavoriteTimeCard(
    favorite: WorryFavoriteTime,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    val backgroundColor = if (selected) AccentCream100 else White800
    val borderColor = if (selected) Primary800 else Primary300

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = favorite.label, style = Body2_15r, color = Gray800)
        Text(text = "${formatMinutes(favorite.startMinutes)}~${formatMinutes(favorite.endMinutes)}", style = Body3_14r, color = Gray400)
    }
}

private fun formatMinutes(minutes: Int): String = "%02d:%02d".format(minutes / 60, minutes % 60)
