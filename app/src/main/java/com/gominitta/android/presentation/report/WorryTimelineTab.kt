package com.gominitta.android.presentation.report

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Gray400

@Composable
internal fun WorryTimelineTab(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = "타임라인 컴포넌트가 준비되면\n이 영역에 표시됩니다.",
            style = Body2_15r,
            color = Gray400,
            textAlign = TextAlign.Center,
        )
    }
}
