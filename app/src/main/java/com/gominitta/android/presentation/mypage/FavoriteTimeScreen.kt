package com.gominitta.android.presentation.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gominitta.android.R
import com.gominitta.android.presentation.mypage.components.MyPageTopBar
import com.gominitta.android.presentation.mypage.model.FavoriteTimeUiModel
import com.gominitta.android.presentation.mypage.model.FavoriteTimeViewModel
import com.gominitta.android.ui.theme.AccentCream200
import com.gominitta.android.ui.theme.Body2_15r

/**
 * 즐겨찾는 시간 목록 화면 진입점.
 * 수정은 더 이상 바텀시트가 아니라 [FavoriteTimeEditRoute] 페이지로 이동해서 처리한다
 * (onEditClick은 AppNavHost에서 navController.navigate(...)로 연결).
 */
@Composable
fun FavoriteTimeRoute(
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (FavoriteTimeUiModel) -> Unit,
    viewModel: FavoriteTimeViewModel = hiltViewModel(),
) {
    FavoriteTimeScreen(
        favoriteTimes = viewModel.favoriteTimes,
        onBackClick = onBackClick,
        onAddClick = onAddClick,
        onEditClick = onEditClick,
    )
}

@Composable
fun FavoriteTimeScreen(
    favoriteTimes: List<FavoriteTimeUiModel>,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (FavoriteTimeUiModel) -> Unit,
) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
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