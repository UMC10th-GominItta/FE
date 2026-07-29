package com.gominitta.android.presentation.worry.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Heading3_20m

/** 걱정 예약 플로우 공용 상단바 — 뒤로가기 + 가운데 타이틀. */
@Composable
fun WorryTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        // 32x32 아이콘 박스의 왼쪽 끝을 화면 가장자리에서 정확히 20dp로.
        // (IconButton은 내부 기본 여백을 얹어 20dp가 안 맞으므로 clickable Icon 사용)
        Icon(
            painter = painterResource(R.drawable.ic_back),
            contentDescription = "뒤로",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)
                .size(32.dp)
                .clickable(onClick = onBack),
        )

        Text(
            text = title,
            style = Heading3_20m,
            color = Gray800,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}
