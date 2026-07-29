package com.gominitta.android.presentation.recipe.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.presentation.recipe.RecipeItem
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType

/**
 * 레시피 화면에서 사용하는 기본 CTA 버튼.
 *
 * 사용 위치:
 * - D102 시작하기
 * - D102-1 완료하기
 * - D102-2 완료하기
 * - D103 수정 완료하기
 * - D104 이대로 등록하기
 */
@Composable
fun RecipePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFBEACB),
            contentColor = Color(0xFF404040),
            disabledContainerColor = Color(0xFFD9D9D9),
            disabledContentColor = Color(0xFFA6A6A6),
        ),
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.32).sp,
            color = if (enabled) Color(0xFF404040) else Color(0xFFA6A6A6),
        )
    }
}
/**
 * D101 마음 레시피 센터에서 사용하는 레시피 카드.
 *
 * 이 컴포넌트는 RecipeItem을 직접 알지 않는다.
 * title, durationMinutes처럼 화면에 필요한 값만 외부에서 받는다.
 *
 * 이렇게 하면:
 * - components 파일이 특정 데이터 모델에 덜 묶임
 * - RecipeItem 파일을 아직 안 만들어도 컴파일 가능
 * - 나중에 서버 모델/domain 모델이 바뀌어도 컴포넌트 수정 범위가 줄어듦
 */
@Composable
fun RecipeCard(
    title: String,
    durationMinutes: Int,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEFFFB),
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = Color(0xFFFBEACB),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_recipe_leaf),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = (-0.32).sp,
                        color = Color(0xFF404040),
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "예상 시간: ${durationMinutes}분",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = (-0.28).sp,
                        color = Color(0xFF404040),
                    )
                }
            }

            Text(
                text = "수정/삭제",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-0.28).sp,
                color = Color(0xFFA6A6A6),
                modifier = Modifier.clickable {
                    onEditClick()
                },
            )
        }
    }
}
/**
 * D103, D104에서 사용하는 입력 필드.
 *
 * 사용 위치:
 * - 레시피 명
 * - 수행 방법
 * - 예상 소요 시간
 */
@Composable
fun RecipeInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    digitsOnly: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.3).sp,
            color = Color(0xFF404040),
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                if (digitsOnly) {
                    onValueChange(input.filter { it.isDigit() })
                } else {
                    onValueChange(input)
                }
            },
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.32).sp,
                    color = Color(0xFFA6A6A6),
                )
            },
            singleLine = singleLine,
            minLines = minLines,
            textStyle = TextStyle(
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.32).sp,
                color = Color(0xFF404040),
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (digitsOnly) KeyboardType.Number else KeyboardType.Text,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(if (singleLine) 64.dp else 132.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFD0C1AB),
                unfocusedBorderColor = Color(0xFFE8DDCC),
                focusedContainerColor = Color(0xFFFEFFFB),
                unfocusedContainerColor = Color(0xFFFEFFFB),
                cursorColor = Color(0xFF404040),
                focusedTextColor = Color(0xFF404040),
                unfocusedTextColor = Color(0xFF404040),
            ),
        )
    }
}
/**
 * D102 레시피 실행 화면에서 사용하는 원형 타이머.
 *
 * Ready 상태:
 * - mainText = "5:00"
 * - subText = "준비되면 시작해요"
 *
 * Running 상태:
 * - mainText = "4:21"
 * - subText = "천천히 진행 중이에요"
 *
 * Completed 상태:
 * - mainText = "완료"
 * - subText = ""
 *
 * 완료 화면에는 별도 체크 아이콘을 두지 않고,
 * 원형 타이머 안쪽 문구만 "완료"로 변경한다.
 */
@Composable
fun RecipeTimerCircle(
    mainText: String,
    subText: String,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(207.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFD0C1AB),
            trackColor = Color(0xFFECDFCE),
            strokeWidth = 18.dp,
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = mainText,
                fontSize = if (mainText == "완료") 42.sp else 46.sp,
                lineHeight = 56.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )

            if (subText.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = subText,
                    fontSize = 18.sp,
                    lineHeight = 25.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/**
 * D104 새 레시피 등록 화면에서 사용하는 추천 칩.
 *
 * 추천칩이란:
 * - "심호흡 5번 하기" 같은 추천 레시피 버튼
 * - 클릭하면 레시피 명/수행 방법/시간 입력칸을 자동으로 채우는 용도
 *
 * 현재 추천 내용은 임시값으로 넣고,
 * 기획 확정 후 텍스트와 연결 데이터를 교체하면 된다.
 */
@Composable
fun RecipeRecommendChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    Surface(
        modifier = modifier
            .height(42.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = if (selected) Color(0xFFFBEACB) else Color(0xFFFEFFFB),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (selected) Color(0xFFE8D29D) else Color(0xFFE2D8C8),
        ),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.32).sp,
                color = Color(0xFF534B42),
            )
        }
    }
}
/**
 * D101 우측 하단 새 레시피 추가 버튼.
 *
 * drawable/ic_recipe_downplus 리소스를 사용한다.
 */
@Composable
fun RecipeAddFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_recipe_downplus),
            contentDescription = "새 레시피 추가",
            modifier = Modifier.size(28.dp)
        )
    }
}

