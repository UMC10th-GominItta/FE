package com.gominitta.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.theme.AccentCream200
import com.gominitta.android.ui.theme.Button1_15m
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.White800

/** 버튼 스타일 — [Filled] 채운 버튼 / [Outlined] 테두리 버튼. */
enum class GominittaButtonVariant { Filled, Outlined }

/**
 * 공통 버튼 — 두 스타일. 둘 다 라운드 12dp, 그림자 없음, 텍스트 진한 브라운(#534B42).
 * - **Filled**  : 배경 #FBEACB (AccentCream200).
 * - **Outlined**: 배경 #FEFEFB (White800) + 1.5dp #FBEACB 테두리.
 *
 * 앞쪽 아이콘이 필요하면 [leadingIcon] 슬롯 사용 (예: `+ 걱정 예약하기`, `▶ 세션 시작`).
 */
@Composable
fun GominittaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: GominittaButtonVariant = GominittaButtonVariant.Filled,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
) {
    val shape: Shape = RoundedCornerShape(12.dp)
    val container = when (variant) {
        GominittaButtonVariant.Filled   -> AccentCream200   // #FBEACB
        GominittaButtonVariant.Outlined -> White800         // #FEFEFB
    }
    val border = when (variant) {
        GominittaButtonVariant.Filled   -> null
        GominittaButtonVariant.Outlined -> BorderStroke(1.5.dp, AccentCream200)
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = Primary800,   // #534B42
        ),
        border = border,
        elevation = null,                // flat (no shadow)
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, style = Button1_15m)
    }
}

// ---- Preview ---------------------------------------------------------------

@Preview(name = "GominittaButton", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun GominittaButtonPreview() {
    GominittaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GominittaButton("걱정 예약하기", onClick = {})
            GominittaButton("한 줄 보태기", onClick = {}, variant = GominittaButtonVariant.Outlined)
            GominittaButton("비활성", onClick = {}, enabled = false)
        }
    }
}
