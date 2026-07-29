package com.gominitta.android.presentation.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.presentation.mypage.components.FavoriteTimeCard
import com.gominitta.android.presentation.mypage.components.MyPagePrimaryButton
import com.gominitta.android.presentation.mypage.components.MyPageTimePicker
import com.gominitta.android.presentation.mypage.components.MyPageTopBar
import com.gominitta.android.presentation.mypage.model.FavoriteTimeUiModel
import com.gominitta.android.presentation.mypage.model.TimeValue
import com.gominitta.android.ui.theme.AccentCream200
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Heading3_20m
import com.gominitta.android.ui.theme.Primary200

@Composable
fun FavoriteTimeRoute(
    favoriteTimes: SnapshotStateList<FavoriteTimeUiModel>,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
) {
    var editingTime by remember {
        mutableStateOf<FavoriteTimeUiModel?>(null)
    }

    // 팝업(바텀시트) 콘텐츠 상단의 window 좌표
    var sheetTopY by remember { mutableStateOf(0f) }

    FavoriteTimeScreen(
        favoriteTimes = favoriteTimes,
        isEditing = editingTime != null,
        sheetTopY = sheetTopY,
        onBackClick = onBackClick,
        onAddClick = onAddClick,
        onEditClick = {
            editingTime = it
        },
    )

    editingTime?.let { target ->
        FavoriteTimeEditBottomSheet(
            favoriteTime = target,
            onDismissRequest = {
                editingTime = null
            },
            onSaveClick = { updated ->
                val index = favoriteTimes.indexOfFirst { it.id == updated.id }
                if (index >= 0) {
                    favoriteTimes[index] = updated
                }
                editingTime = null
            },
            onDeleteClick = {
                favoriteTimes.removeAll { it.id == target.id }
                editingTime = null
            },
            onSheetPositioned = { sheetTopY = it },
        )
    }
}

@Composable
fun FavoriteTimeScreen(
    favoriteTimes: List<FavoriteTimeUiModel>,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (FavoriteTimeUiModel) -> Unit,
    isEditing: Boolean = false,
    sheetTopY: Float = 0f,
) {
    val density = LocalDensity.current
    var boxBottomY by remember { mutableStateOf(0f) } // 이 Box 하단의 window y좌표

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            MyPageTopBar(
                title = "즐겨찾는 시간 관리",
                onBackClick = onBackClick,
            )
        },
        floatingActionButton = {
            IconButton(
                onClick = onAddClick,
                modifier = Modifier.size(56.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_mypage_downplus),
                    contentDescription = "즐겨찾는 시간 추가",
                    modifier = Modifier.size(56.dp),
                    tint = Color.Unspecified,
                )
            }
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 80.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(
                        text = "자주 사용하는 걱정 예약 시간을\n등록하고 관리해 보세요. (최대 4개)",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 18.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }

                if (favoriteTimes.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 82.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "즐겨찾는 시간이 없습니다.",
                                style = Body2_15r,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                            )
                        }
                    }
                } else {
                    items(
                        items = favoriteTimes,
                        key = { it.id },
                    ) { item ->
                        FavoriteTimeListItem(
                            item = item,
                            onEditClick = { onEditClick(item) },
                        )
                    }
                }
            }

            // 이미지 하단이 팝업 상단(sheetTopY)에 정확히 맞닿도록, Box 하단 기준으로 위로 밀어올림
            if (isEditing && sheetTopY > 0f && boxBottomY > 0f) {
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
}

@Composable
private fun FavoriteTimeListItem(
    item: FavoriteTimeUiModel,
    onEditClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = AccentCream200,
                spotColor = AccentCream200,
            )
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_mypage_leaf),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified,
        )

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )

            Text(
                text = "${item.startTime.formatted()}-${item.endTime.formatted()}",
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(
            text = "수정/삭제",
            modifier = Modifier.clickable(onClick = onEditClick),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textDecoration = TextDecoration.Underline,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteTimeEditBottomSheet(
    favoriteTime: FavoriteTimeUiModel,
    onDismissRequest: () -> Unit,
    onSaveClick: (FavoriteTimeUiModel) -> Unit,
    onDeleteClick: () -> Unit,
    onSheetPositioned: (Float) -> Unit = {},
) {
    var startTime by remember(favoriteTime.id) { mutableStateOf(favoriteTime.startTime) }
    var endTime by remember(favoriteTime.id) { mutableStateOf(favoriteTime.endTime) }
    var editingField by remember { mutableStateOf<EditingField?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
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
                    onSheetPositioned(coordinates.positionInWindow().y)
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
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = favoriteTime.title, style = Heading3_20m)

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.align(Alignment.CenterEnd),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mypage_trash),
                            contentDescription = "즐겨찾는 시간 삭제",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified,
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                ) {
                    FavoriteTimeCard(
                        time = startTime,
                        suffix = "부터",
                        selected = editingField == EditingField.START,
                        onClick = { editingField = EditingField.START },
                    )
                    FavoriteTimeCard(
                        time = endTime,
                        suffix = "까지",
                        selected = editingField == EditingField.END,
                        onClick = { editingField = EditingField.END },
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(top = 28.dp)
                        .fillMaxWidth()
                        .height(135.dp),
                ) {
                    if (editingField != null) {
                        MyPageTimePicker(
                            value = if (editingField == EditingField.START) startTime else endTime,
                            onValueChange = { updated ->
                                if (editingField == EditingField.START) {
                                    startTime = updated
                                } else {
                                    endTime = updated
                                }
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                MyPagePrimaryButton(
                    text = "저장하기",
                    onClick = {
                        onSaveClick(
                            favoriteTime.copy(startTime = startTime, endTime = endTime),
                        )
                    },
                )
            }
        }
    }
}