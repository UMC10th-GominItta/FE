package com.gominitta.android.presentation.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.presentation.mypage.components.FavoriteTimeCard
import com.gominitta.android.presentation.mypage.components.MyPagePrimaryButton
import com.gominitta.android.presentation.mypage.components.MyPageTimePicker
import com.gominitta.android.presentation.mypage.components.MyPageTopBar
import com.gominitta.android.presentation.mypage.model.FavoriteTimeUiModel
import com.gominitta.android.presentation.mypage.model.TimeValue
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Primary200

/**
 * 즐겨찾는 시간 수정 페이지.
 * 기존 FavoriteTimeEditBottomSheet(ModalBottomSheet)를 대체 —
 * FavoriteTimeAddScreen과 동일한 "시간선택 바텀시트 + 그라데이션 오버레이" UX를 그대로 재사용한다.
 * 별도 ViewModel 없이 진입 시점의 [favoriteTime] 값으로 로컬 편집 상태를 초기화하고,
 * 저장/삭제는 콜백으로 상위(AppNavHost)에 위임해 리스트 화면과 같은 FavoriteTimeViewModel이 처리한다.
 */
@Composable
fun FavoriteTimeEditRoute(
    favoriteTime: FavoriteTimeUiModel,
    onBackClick: () -> Unit,
    onSaved: (FavoriteTimeUiModel) -> Unit,
    onDeleteClick: (FavoriteTimeUiModel) -> Unit,
) {
    var title by remember(favoriteTime.id) {
        mutableStateOf(favoriteTime.title)
    }

    var startTime by remember(favoriteTime.id) {
        mutableStateOf(favoriteTime.startTime)
    }

    var endTime by remember(favoriteTime.id) {
        mutableStateOf(favoriteTime.endTime)
    }

    var editingField by remember(favoriteTime.id) {
        mutableStateOf<EditingField?>(null)
    }

    FavoriteTimeEditScreen(
        title = title,
        startTime = startTime,
        endTime = endTime,
        editingField = editingField,
        onTitleChange = {
            title = it.take(20)
        },
        onStartTimeClick = {
            editingField = EditingField.START
        },
        onEndTimeClick = {
            editingField = EditingField.END
        },
        onTimeChange = { updated ->
            when (editingField) {
                EditingField.START -> startTime = updated
                EditingField.END -> endTime = updated
                null -> Unit
            }
        },
        onTimePickerDismiss = {
            editingField = null
        },
        onSaveClick = {
            onSaved(
                favoriteTime.copy(
                    title = title.trim(),   // ← 이게 있어야 이름이 저장됨
                    startTime = startTime,
                    endTime = endTime,
                ),
            )
        },
        onDeleteClick = {
            onDeleteClick(favoriteTime)
        },
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteTimeEditScreen(
    title: String,
    startTime: TimeValue,
    endTime: TimeValue,
    editingField: EditingField?,
    onTitleChange: (String) -> Unit,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit,
    onTimeChange: (TimeValue) -> Unit,
    onTimePickerDismiss: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val density = LocalDensity.current

    var sheetTopY by remember { mutableStateOf(0f) }
    var boxBottomY by remember { mutableStateOf(0f) } // 콘텐츠 Box 하단의 window y좌표

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            MyPageTopBar(
                title = "즐겨찾는 시간 수정",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mypage_trash),
                            contentDescription = "즐겨찾는 시간 삭제",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .onGloballyPositioned { coordinates ->
                    boxBottomY = coordinates.positionInWindow().y + coordinates.size.height
                },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        })
                    }
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp),
            ) {
                Text(
                    text = "즐겨찾는 시간 이름",
                    style = MaterialTheme.typography.labelLarge,
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    placeholder = {
                        Text(text = "이름을 입력해주세요.")
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                ) {
                    FavoriteTimeCard(
                        time = startTime,
                        suffix = "부터",
                        selected = editingField == EditingField.START,
                        onClick = onStartTimeClick,
                    )

                    FavoriteTimeCard(
                        time = endTime,
                        suffix = "까지",
                        selected = editingField == EditingField.END,
                        onClick = onEndTimeClick,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                MyPagePrimaryButton(
                    text = "저장하기",
                    onClick = onSaveClick,
                    enabled = title.isNotBlank(),
                    modifier = Modifier.padding(bottom = 18.dp),
                )
            }

            // 이미지 하단이 팝업 상단(sheetTopY)에 정확히 맞닿도록, Box 하단 기준으로 위로 밀어올림
            if (editingField != null && sheetTopY > 0f && boxBottomY > 0f) {
                val liftPx = boxBottomY - sheetTopY
                val liftDp = with(density) { liftPx.toDp() }

                Image(
                    painter = painterResource(R.drawable.ic_mypage_favorite_editgrad),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = -liftDp)
                        .fillMaxWidth()
                        .height(253.dp),
                )
            }
        }
    }

    if (editingField != null) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

        ModalBottomSheet(
            onDismissRequest = onTimePickerDismiss,
            sheetState = sheetState,
            containerColor = Primary200,
            scrimColor = Color.Transparent,
            shape = RoundedCornerShape(0.dp),
            dragHandle = null,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .onGloballyPositioned { coordinates ->
                        sheetTopY = coordinates.positionInWindow().y
                    },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(Gray800),
                )

                Box(
                    modifier = Modifier
                        .padding(top = 17.dp)
                        .align(Alignment.CenterHorizontally)
                        .size(width = 50.dp, height = 3.dp)
                        .background(Color.Black, RoundedCornerShape(1.5.dp)),
                )

                Column(
                    modifier = Modifier.padding(horizontal = 21.dp, vertical = 18.dp),
                ) {
                    MyPageTimePicker(
                        value = if (editingField == EditingField.START) startTime else endTime,
                        onValueChange = onTimeChange,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    MyPagePrimaryButton(
                        text = "저장하기",
                        onClick = onTimePickerDismiss,
                    )
                }
            }
        }
    }
}