package com.gominitta.android.presentation.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gominitta.android.presentation.mypage.components.MyPagePrimaryButton
import com.gominitta.android.presentation.mypage.components.MyPageTopBar
import androidx.compose.ui.graphics.Color

@Composable
fun ProfileEditRoute(
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
) {
    var nickname by rememberSaveable {
        mutableStateOf("")
    }

    var selectedProfileIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    ProfileEditScreen(
        nickname = nickname,
        initialNickname = "00님",
        selectedProfileIndex = selectedProfileIndex,
        saveEnabled = nickname.isNotBlank() || selectedProfileIndex != 0,
        onNicknameChange = {
            nickname = it.take(12)
        },
        onProfileSelected = {
            selectedProfileIndex = it
        },
        onSaveClick = onSaved,
        onBackClick = onBackClick,
    )
}

@Composable
fun ProfileEditScreen(
    nickname: String,
    initialNickname: String,
    selectedProfileIndex: Int,
    saveEnabled: Boolean,
    onNicknameChange: (String) -> Unit,
    onProfileSelected: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            MyPageTopBar(
                title = "프로필 수정",
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            Text(
                text = "닉네임 변경",
                style = MaterialTheme.typography.labelLarge,
            )

            OutlinedTextField(
                value = nickname,
                onValueChange = onNicknameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                placeholder = {
                    Text(text = "기본: $initialNickname")
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
            )

            Text(
                text = "프로필 이미지 변경",
                modifier = Modifier.padding(top = 22.dp),
                style = MaterialTheme.typography.labelLarge,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                repeat(5) { index ->
                    ProfileImageOption(
                        index = index,
                        selected = selectedProfileIndex == index,
                        onClick = {
                            onProfileSelected(index)
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            MyPagePrimaryButton(
                text = "저장하기",
                onClick = onSaveClick,
                enabled = saveEnabled,
                modifier = Modifier.padding(bottom = 18.dp),
            )
        }
    }
}

@Composable
private fun ProfileImageOption(
    index: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val alphaList = listOf(
        0.9f,
        0.75f,
        0.6f,
        0.5f,
        0.4f,
    )

    Box(
        modifier = Modifier
            .size(62.dp)
            .then(
                if (selected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                    )
                } else {
                    Modifier
                },
            )
            .padding(3.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(
                    alpha = alphaList[index],
                ),
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
    )
}