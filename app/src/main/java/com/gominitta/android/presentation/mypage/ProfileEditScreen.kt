package com.gominitta.android.presentation.mypage

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gominitta.android.presentation.mypage.components.MyPagePrimaryButton
import com.gominitta.android.presentation.mypage.components.MyPageTopBar
import com.gominitta.android.presentation.mypage.model.ProfileImages
import com.gominitta.android.ui.theme.Primary800

@Composable
fun ProfileEditRoute(
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
) {
    val viewModel: ProfileEditViewModel = hiltViewModel()
    ProfileEditScreen(
        nickname = viewModel.nickname,
        initialNickname = viewModel.initialNickname,
        selectedProfileIndex = viewModel.selectedProfileIndex,
        saveEnabled = viewModel.nickname.isNotBlank() || viewModel.selectedProfileIndex != ProfileImages.DEFAULT_INDEX,
        onNicknameChange = viewModel::onNicknameChange,
        onProfileSelected = viewModel::onProfileSelected,
        onSaveClick = {
            viewModel.save()
            onSaved()
        },
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
                ProfileImages.all.forEachIndexed { index, _ ->
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
    Box(
        modifier = Modifier
            .size(62.dp)
            .then(
                if (selected) {
                    Modifier.border(
                        width = 2.dp,
                        color = Primary800,
                        shape = CircleShape,
                    )
                } else {
                    Modifier
                },
            )
            .padding(3.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(ProfileImages.all[index]),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}