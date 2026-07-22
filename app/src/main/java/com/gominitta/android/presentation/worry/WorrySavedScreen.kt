package com.gominitta.android.presentation.worry

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.presentation.worry.components.WorryPrimaryButton
import com.gominitta.android.presentation.worry.components.WorrySessionInfoCard
import com.gominitta.android.ui.components.GominittaBackground
import com.gominitta.android.ui.theme.AccentCream200
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading4_18m
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title2_18sb
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val DateFormatter = DateTimeFormatter.ofPattern("MM/dd")
private val TimeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

/**
 * 걱정 보관 완료 (B103) — 예약된 마음 세션 정보 카드를 보여준다.
 * 완료 화면이라 뒤로가기를 누르면 이전 화면으로 돌아가지 않고 [onNavigateToHome]을 그대로 호출한다.
 */
@Composable
fun WorrySavedScreen(
    onNavigateToHome: () -> Unit,
    startTime: LocalDateTime? = null,
    endTime: LocalDateTime? = null,
    modifier: Modifier = Modifier,
) {
    val sampleStartTime = remember { LocalDateTime.of(LocalDate.now().year, 4, 14, 22, 0) }
    val sampleEndTime = remember { LocalDateTime.of(LocalDate.now().year, 4, 14, 23, 0) }
    val start = startTime ?: sampleStartTime
    val end = endTime ?: sampleEndTime

    BackHandler { onNavigateToHome() }

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(120.dp))

                Text(
                    text = "걱정이 잘 보관되었어요.",
                    style = Heading4_18m,
                    color = Gray800,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(40.dp))

                Image(
                    painter = painterResource(R.drawable.worry_saved_folder),
                    contentDescription = null,
                    modifier = Modifier.size(width = 45.dp, height = 50.dp),
                    contentScale = ContentScale.Fit,
                )

                Spacer(Modifier.height(40.dp))

                Text(
                    text = buildAnnotatedString {
                        append("나의 하루를 위해 잠시 접어두고,\n")
                        withStyle(Title2_18sb.toSpanStyle()) {
                            append(formatSessionTime(start))
                        }
                        append(" 에 만나요!")
                    },
                    style = Heading4_18m,
                    color = Gray800,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(52.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(AccentCream200),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_clock),
                            contentDescription = null,
                            tint = Primary800,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(text = "예약된 마음 세션", style = Body1_16m, color = Gray800)
                }

                Spacer(Modifier.height(18.dp))

                WorrySessionInfoCard(
                    startTime = start,
                    endTime = end,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            WorryPrimaryButton(
                text = "일상으로 돌아가기",
                onClick = onNavigateToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp),
            )
        }
    }
}

// ---- 날짜/시간 포맷 ----------------------------------------------------------

private fun formatSessionTime(dateTime: LocalDateTime): String {
    val date = dateTime.format(DateFormatter)
    val weekday = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)
    val time = dateTime.format(TimeFormatter)
    return "$date $weekday $time"
}

// ---- Preview ---------------------------------------------------------------

@Preview(name = "WorrySaved", showBackground = true, backgroundColor = 0xFFFEFEFB, widthDp = 375, heightDp = 812)
@Composable
private fun WorrySavedScreenPreview() {
    GominittaTheme {
        GominittaBackground {
            WorrySavedScreen(onNavigateToHome = {})
        }
    }
}
