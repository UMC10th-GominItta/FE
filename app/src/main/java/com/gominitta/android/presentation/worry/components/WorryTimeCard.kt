package com.gominitta.android.presentation.worry.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.ui.theme.AccentCream100
import com.gominitta.android.ui.theme.AccentCream200
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Button1_15m
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Heading4_18m
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary400
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title2_18sb
import com.gominitta.android.ui.theme.White800
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val DateFormatter = DateTimeFormatter.ofPattern("MM/dd")
private val TimeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

/** 시간 카드 상태 — 아직 설정 안 함 / 편집 중(시트 열림) / 설정 완료. */
enum class WorryTimeCardState { Unset, Editing, Set }

/** 걱정 예약 시작/종료 시간 카드 — 날짜·요일·시간을 보여주고 탭하면 시간 선택 시트를 연다. */
@Composable
fun WorryTimeCard(
    dateTime: LocalDateTime,
    suffix: String,
    state: WorryTimeCardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when (state) {
        WorryTimeCardState.Unset -> White800
        WorryTimeCardState.Editing -> AccentCream100
        WorryTimeCardState.Set -> White800
    }
    val borderColor = when (state) {
        WorryTimeCardState.Unset -> Primary200
        WorryTimeCardState.Editing -> Primary400
        WorryTimeCardState.Set -> Primary800
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp, end = 11.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(AccentCream200),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_pencil),
                contentDescription = null,
                tint = Primary800,
                modifier = Modifier.size(20.dp),
            )
        }

        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp)) {
            Row {
                Text(formatDate(dateTime), style = Heading4_18m, color = Gray800, maxLines = 1, modifier = Modifier.alignByBaseline())
                Spacer(Modifier.width(4.dp))
                Text(formatWeekday(dateTime), style = Button1_15m, color = Gray800, maxLines = 1, modifier = Modifier.alignByBaseline())
            }
            Row {
                Text(formatTime(dateTime), style = Title2_18sb, color = Gray800, maxLines = 1, modifier = Modifier.alignByBaseline())
                Spacer(Modifier.width(4.dp))
                Text(suffix, style = Body2_15r, color = Gray800, maxLines = 1, modifier = Modifier.alignByBaseline())
            }
        }
    }
}

// ---- 날짜/시간 포맷 ----------------------------------------------------------

private fun formatDate(dateTime: LocalDateTime): String = dateTime.format(DateFormatter)

private fun formatWeekday(dateTime: LocalDateTime): String =
    dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)

private fun formatTime(dateTime: LocalDateTime): String = dateTime.format(TimeFormatter)
