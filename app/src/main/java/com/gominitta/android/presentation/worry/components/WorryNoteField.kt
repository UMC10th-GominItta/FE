package com.gominitta.android.presentation.worry.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Primary300
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.White800

private const val TITLE_MAX = 50
private const val CONTENT_MAX = 500

/** 테이프가 달린 메모지 스타일 걱정 제목·내용 입력창. */
@Composable
fun WorryNoteField(
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(White800)
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            BasicTextField(
                value = title,
                onValueChange = { if (it.length <= TITLE_MAX) onTitleChange(it) },
                singleLine = true,
                textStyle = Body1_16m.copy(color = Gray800),
                cursorBrush = SolidColor(Primary800),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    Box {
                        if (title.isEmpty()) {
                            Text("제목", style = Body1_16m, color = Gray400)
                        }
                        inner()
                    }
                },
            )

            Spacer(Modifier.height(8.dp))

            BasicTextField(
                value = content,
                onValueChange = { if (it.length <= CONTENT_MAX) onContentChange(it) },
                textStyle = Body2_15r.copy(color = Gray800),
                cursorBrush = SolidColor(Primary800),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 122.dp),
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

// ---- Preview ---------------------------------------------------------------

@Preview(name = "WorryNoteField 빈 상태", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun WorryNoteFieldEmptyPreview() {
    GominittaTheme {
        WorryNoteField(
            title = "",
            onTitleChange = {},
            content = "",
            onContentChange = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "WorryNoteField 입력됨", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun WorryNoteFieldFilledPreview() {
    GominittaTheme {
        WorryNoteField(
            title = "UMC 프로젝트가 안 구해지면 어떡하지",
            onTitleChange = {},
            content = "걱정걱정걱정",
            onContentChange = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
