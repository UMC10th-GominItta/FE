package com.gominitta.android.presentation.worry.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Primary300
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.White800

private const val MEMO_MAX = 100

/** 테이프가 달린 메모지 스타일 한 줄 보태기 입력창 — 제목 없이 내용만 받는 [WorryNoteField]의 축소판. */
@Composable
fun WorryMemoField(
    content: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(White800)
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            BasicTextField(
                value = content,
                onValueChange = { if (it.length <= MEMO_MAX) onContentChange(it) },
                textStyle = Body2_15r.copy(color = Gray800),
                cursorBrush = SolidColor(Primary800),
                modifier = Modifier.fillMaxSize(),
                decorationBox = { inner ->
                    Box {
                        if (content.isEmpty()) {
                            Text("걱정을 입력해주세요", style = Body2_15r, color = Gray400)
                        }
                        inner()
                    }
                },
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
                .size(width = 84.dp, height = 32.dp)
                .background(Primary300),
        )
    }
}
