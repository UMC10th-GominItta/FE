package com.gominitta.android.presentation.worry.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Button1_15m
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Primary300
import com.gominitta.android.ui.theme.White800
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val DateFormatter = DateTimeFormatter.ofPattern("MM/dd")
private val TimeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

/** 예약된 마음 세션의 시작/종료 시각을 보여주는 카드 — 두 행 사이에 구분선. */
@Composable
fun WorrySessionInfoCard(
    startTime: LocalDateTime,
    endTime: LocalDateTime,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(White800)
            .border(1.dp, Primary300, shape),
    ) {
        WorrySessionInfoRow(dateTime = startTime, label = "시작")
        HorizontalDivider(thickness = 1.dp, color = Primary300)
        WorrySessionInfoRow(dateTime = endTime, label = "종료")
    }
}

@Composable
private fun WorrySessionInfoRow(dateTime: LocalDateTime, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = formatSessionTime(dateTime), style = Button1_15m, color = Gray800)
        Text(text = label, style = Body3_14r, color = Gray800)
    }
}

// ---- 날짜/시간 포맷 ----------------------------------------------------------

private fun formatSessionTime(dateTime: LocalDateTime): String {
    val date = dateTime.format(DateFormatter)
    val weekday = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)
    val time = dateTime.format(TimeFormatter)
    return "$date $weekday $time"
}
