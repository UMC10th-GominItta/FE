package com.gominitta.android.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.gominitta.android.R
import com.gominitta.android.ui.theme.White800

/**
 * 앱 공통 배경 — bg_main 이미지를 화면 뒤에 한 번 깔고 그 위에 [content] 를 얹는다.
 * MainActivity 에서 AppNavHost 를 감싸 전 화면에 적용. 이미지가 보이려면 각 화면의
 * Scaffold/루트가 투명해야 함(containerColor = Color.Transparent, 불투명 background 금지).
 */
@Composable
fun GominittaBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize().background(White800)) {
        Image(
            painter = painterResource(R.drawable.bg_main),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.4f,   // 배경 이미지 불투명도 40% (White800 위에)
            modifier = Modifier.matchParentSize(),
        )
        content()
    }
}
