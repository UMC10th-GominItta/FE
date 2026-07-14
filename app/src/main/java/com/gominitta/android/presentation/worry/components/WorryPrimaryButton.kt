package com.gominitta.android.presentation.worry.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.components.GominittaButton
import com.gominitta.android.ui.components.GominittaButtonVariant
import com.gominitta.android.ui.theme.Gray200
import com.gominitta.android.ui.theme.Gray600

/**
 * 걱정 예약 플로우 공용 CTA 버튼 (다음/저장/완료 등).
 * Primary 스타일 + 그림자 + 비활성 색(#D4D4D4)을 고정해, 화면마다 스타일을 반복 지정하지 않는다.
 * 위치·활성 여부만 [modifier]/[enabled]로 받는다.
 */
@Composable
fun WorryPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    GominittaButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = GominittaButtonVariant.Primary,
        enabled = enabled,
        shadowElevation = 8.dp,
        shadowColor = Color.Black.copy(alpha = 0.04f),
        disabledContainerColor = Gray200,   // #D4D4D4
        disabledContentColor = Gray600,     // #737373
    )
}
