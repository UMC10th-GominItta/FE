package com.gominitta.android.presentation.mypage.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.presentation.mypage.model.TimeValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.width
import com.gominitta.android.ui.theme.Primary300
import com.gominitta.android.ui.theme.Title2_18sb
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.shadow
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Primary200

val PretendardFontFamily = FontFamily(
    Font(R.font.pretendard_medium, FontWeight.Medium)
)

@Composable
fun MyPageSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        modifier = modifier,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun MyPageSettingRow(
    @DrawableRes iconRes: Int,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
) {
    val contentColor = if (enabled) {
        com.gominitta.android.ui.theme.Gray800          // 기존: MaterialTheme.colorScheme.onSurface
    } else {
        com.gominitta.android.ui.theme.Gray800.copy(alpha = 0.35f)   // 기존: onSurface.copy(alpha = 0.35f)
    }

    // 중복으로 감싸져 있던 Row 하나를 제거하여 깔끔하게 수정했습니다.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                enabled = enabled,
                onClick = onClick,
            )
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp, // 피그마 규격에 맞게 상하 패딩 조정 (필요시 원래 14.dp로 유지 가능)
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp), // 피그마 좌측 아이콘 크기
            tint = Color.Unspecified,
        )

        Text(
            text = title,
            modifier = Modifier
                .padding(start = 12.dp) // 아이콘과 텍스트 사이 간격 12.dp
                .weight(1f),
            // Pretendard Medium 16px 스타일 적용
            fontFamily = PretendardFontFamily, // 이전 질문에서 만드신 FontFamily 사용
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = contentColor,
        )

        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_mypage_rightarrow,
                ),
                contentDescription = null,
                modifier = Modifier.size(24.dp), // 우측 화살표 아이콘 크기도 피그마에 맞춰 24.dp로 조정하는 것을 권장합니다.
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
fun MyPageProfileCard(
    nickname: String,
    email: String,
    onProfileEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                    ),
            )

            Column(
                modifier = Modifier
                    .padding(start = 14.dp)
                    .weight(1f),
            ) {
                Text(
                    text = nickname,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )

                Text(
                    text = email,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            TextButton(onClick = onProfileEditClick) {
                Text(
                    text = "프로필 수정",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = TextDecoration.Underline,
                )
            }
        }
    }
}
@Composable
fun MyPagePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()   // 부모가 20dp 좌우 패딩을 준 popup 안에서는 이미 335dp 근사치라 width 직접 지정 불필요
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,      // ✅ 수정: primaryContainer(#FBEACB) → primary(Accent/Cream/300 #F9E0BA)
            contentColor = MaterialTheme.colorScheme.onPrimary,       // ✅ 수정: 위와 짝 맞춤
            disabledContainerColor = com.gominitta.android.ui.theme.Gray200,   // ✅ 수정: surfaceVariant → Gray200(#D4D4D4)
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,   // 이미 Button1_15m = Pretendard Medium 15px
        )
    }
}
@Composable
fun MyPageOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
@Composable
fun FavoriteTimeCard(
    time: TimeValue,
    suffix: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.tertiaryContainer   // AccentCream100 = #FDF1D6
    } else {
        MaterialTheme.colorScheme.surface              // White800 = #FEFEFB
    }

    Box(
        modifier = modifier
            .width(166.dp)
            .height(86.dp)
            .shadow(                                    // ✅ 추가: X0 Y3 blur16 spread0 #000000 4%
                elevation = 16.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.04f),
            )
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = Primary200,
                shape = RoundedCornerShape(14.dp),   // clip과 동일하게 14dp로 통일
            )

            .clickable(onClick = onClick),
        // 바깥 Box의 padding(horizontal = 12.dp)은 삭제 —
        // 이제 아래 Row가 자체 패딩으로 텍스트 위치를 담당함
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                start = 20.dp,
                top = 45.dp,     // 글씨를 아래로 내리는 값 — top만 크게
                end = 20.dp,
                bottom = 20.dp,
            ),
        ) {
            Text(
                text = time.formatted(),
                style = Title2_18sb,
                color = MaterialTheme.colorScheme.onSurface,   // #404040
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = suffix,
                style = Body2_15r,   // Pretendard Regular 15
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_mypage_edit),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 11.dp)
                .size(32.dp),
            tint = Color.Unspecified,
        )
    }
}