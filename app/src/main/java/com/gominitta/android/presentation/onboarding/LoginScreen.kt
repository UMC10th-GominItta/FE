package com.gominitta.android.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.ui.components.KakaoLoginButton
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Gray600
import com.gominitta.android.ui.theme.Heading1_24sb
import com.gominitta.android.ui.theme.Heading4_18m
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb

/**
 * 로그인 화면 — 카카오 로그인 진입. 온보딩 후 진입.
 * 실제 카카오 SDK 연동은 미구현(버튼 클릭 시 로그인 완료 화면으로).
 */
@Composable
fun LoginScreen(
    onLoginComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Gray600)) {
                        append("불안한 고민은 ")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = Primary800)) {
                        append("이따")
                    }
                    withStyle(SpanStyle(color = Gray600)) {
                        append(" 마주할 수 있도록,\n지금 당신의 일상을 지켜드릴게요.")
                    }
                },
                style = Heading4_18m,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.login_hero),
                contentDescription = "고민이따",
                modifier = Modifier.size(width = 162.dp, height = 180.dp),
            )

            Spacer(Modifier.height(48.dp))

            Text(text = "고민이따", style = Heading1_24sb, color = Primary800)

            Spacer(Modifier.height(12.dp))

            Text(
                text = "걱정은 잠시 접어두고 이따가 마주해요.",
                style = Heading4_18m,
                color = Gray600,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.weight(1.3f))

            KakaoLoginButton(onClick = onLoginComplete, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(16.dp))
        }
    }
}
