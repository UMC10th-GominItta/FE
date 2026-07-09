package com.gominitta.android.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.GominittaTheme

// 카카오 브랜드 옐로 — 카카오 전용 색이라 Color.kt 토큰으로 빼지 않고 이 파일에 둔다.
private val KakaoYellow = Color(0xFFFEE500)

/**
 * 카카오 로그인 버튼 — 라운드 12dp, 그림자 없음, 배경 카카오 옐로.
 * 아이콘은 좌측 고정, 텍스트는 버튼 중앙 정렬.
 */
@Composable
fun KakaoLoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = KakaoYellow,
            contentColor = Color.Black.copy(alpha = 0.85f),
        ),
        contentPadding = PaddingValues(horizontal = 20.dp),
        elevation = null,   // flat (no shadow)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(R.drawable.ic_kakao),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterStart).size(20.dp),
            )
            Text(
                text = "카카오 로그인",
                style = Body1_16m,   // 16sp — 버튼 높이 52dp의 1/3 규칙(카카오 가이드) 충족
                color = Color.Black.copy(alpha = 0.85f),
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

// ---- Preview ---------------------------------------------------------------

@Preview(name = "KakaoLoginButton", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun KakaoLoginButtonPreview() {
    GominittaTheme {
        KakaoLoginButton(onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}
