package com.gominitta.android.presentation.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.gominitta.android.presentation.mypage.components.MyPageTopBar

@Composable
fun NotificationSettingRoute(
    onBackClick: () -> Unit,
    viewModel: NotificationSettingViewModel = hiltViewModel(),
) {
    NotificationSettingScreen(
        reservationReminderEnabled = viewModel.reservationReminderEnabled,
        sessionStartReminderEnabled = viewModel.sessionStartReminderEnabled,
        errorMessage = viewModel.errorMessage,
        onReservationReminderChanged = viewModel::onReservationReminderChanged,
        onSessionStartReminderChanged = viewModel::onSessionStartReminderChanged,
        onBackClick = onBackClick,
    )
}

@Composable
fun NotificationSettingScreen(
    reservationReminderEnabled: Boolean,
    sessionStartReminderEnabled: Boolean,
    errorMessage: String? = null,
    onReservationReminderChanged: (Boolean) -> Unit,
    onSessionStartReminderChanged: (Boolean) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            MyPageTopBar(
                title = "푸시 알림 설정",
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "걱정 예약 시간과 세션 시작 알림을\n놓치지 않고 받아보세요.",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(28.dp))

            NotificationSettingRow(
                title = "걱정 예약 시간 리마인드",
                checked = reservationReminderEnabled,
                onCheckedChange = onReservationReminderChanged,
            )

            Spacer(modifier = Modifier.height(12.dp))

            NotificationSettingRow(
                title = "마음 세션 시작 알림",
                checked = sessionStartReminderEnabled,
                onCheckedChange = onSessionStartReminderChanged,
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun NotificationSettingRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(18.dp),
            )
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        )
    }
}