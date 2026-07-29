package com.gominitta.android.presentation.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.presentation.mypage.components.MyPageOutlinedButton
import com.gominitta.android.presentation.mypage.components.MyPagePrimaryButton
import com.gominitta.android.presentation.mypage.components.MyPageTopBar
import androidx.compose.ui.graphics.Color

@Composable
fun WithdrawScreen(
    onBackClick: () -> Unit,
    onCancelClick: () -> Unit,
    onWithdrawClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            MyPageTopBar(
                title = "회원 탈퇴",
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(0.6f))

            Image(
                painter = painterResource(
                    R.drawable.ic_mypage_withdraw_cat,
                ),
                contentDescription = null,
                modifier = Modifier.size(190.dp),
            )

            Text(
                text = "정말 탈퇴하시겠습니까?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "탈퇴 시, 그동안 기록하신 모든 데이터와\n" +
                        "마음 리포트 내역이 완전히 파기되며\n" +
                        "복구할 수 없습니다.",
                modifier = Modifier.padding(top = 18.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MyPageOutlinedButton(
                    text = "취소",
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f),
                )

                MyPagePrimaryButton(
                    text = "탈퇴하기",
                    onClick = onWithdrawClick,
                    modifier = Modifier.weight(2f),
                )
            }
        }
    }
}