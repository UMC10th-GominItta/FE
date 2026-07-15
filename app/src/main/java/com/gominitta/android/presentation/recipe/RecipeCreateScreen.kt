package com.gominitta.android.presentation.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gominitta.android.presentation.recipe.components.RecipeInputField
import com.gominitta.android.presentation.recipe.components.RecipePrimaryButton
import com.gominitta.android.presentation.recipe.components.RecipeRecommendChip
import com.gominitta.android.presentation.recipe.components.RecipeTopBar

/**
 * D104 / D104-1 새 레시피 등록 화면.
 *
 * 역할:
 * - 추천 레시피 칩을 보여준다.
 * - 추천 칩 클릭 시 입력 필드를 임시 데이터로 채운다.
 * - 직접 입력으로 레시피 명, 수행 방법, 예상 소요 시간을 입력한다.
 * - 입력값이 모두 채워졌을 때 등록 버튼을 활성화한다.
 *
 * 현재 단계:
 * - 단순 UI 구현
 * - 실제 서버 저장/계정별 저장 없음
 * - 등록 버튼 클릭 시 상위 콜백만 호출
 */
@Composable
fun RecipeCreateScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    recommendedRecipes: List<RecommendedRecipe> = sampleRecommendedRecipes,
    onRegisterClick: (
        title: String,
        description: String,
        durationMinutes: Int,
    ) -> Unit = { _, _, _ -> },
) {
    var selectedRecommendedTitle by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var title by rememberSaveable {
        mutableStateOf("")
    }

    var description by rememberSaveable {
        mutableStateOf("")
    }

    var duration by rememberSaveable {
        mutableStateOf("")
    }

    val durationMinutes = duration
        .filter { it.isDigit() }
        .toIntOrNull()

    val isRegisterEnabled = title.isNotBlank() &&
            description.isNotBlank() &&
            durationMinutes != null &&
            durationMinutes > 0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = innerPadding.calculateBottomPadding() + 40.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                RecipeTopBar(
                    title = "새 레시피 등록",
                    onBackClick = onNavigateBack,
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "이런 레시피는 어때요?",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(14.dp))

                RecommendedRecipeChips(
                    recommendedRecipes = recommendedRecipes,
                    selectedTitle = selectedRecommendedTitle,
                    onRecommendedRecipeClick = { recommendedRecipe ->
                        selectedRecommendedTitle = recommendedRecipe.title
                        title = recommendedRecipe.title
                        description = recommendedRecipe.description
                        duration = recommendedRecipe.durationMinutes.toString()
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "직접 입력하기",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            item {
                RecipeInputField(
                    label = "레시피 명",
                    value = title,
                    onValueChange = { newTitle ->
                        title = newTitle
                        selectedRecommendedTitle = null
                    },
                    placeholder = "예: 따뜻한 차 마시기",
                )
            }

            item {
                RecipeInputField(
                    label = "수행 방법",
                    value = description,
                    onValueChange = { newDescription ->
                        description = newDescription
                        selectedRecommendedTitle = null
                    },
                    placeholder = "어떻게 불안을 해소할까요?",
                    singleLine = false,
                    minLines = 4,
                )
            }

            item {
                RecipeInputField(
                    label = "예상 소요 시간",
                    value = duration,
                    onValueChange = { newDuration ->
                        duration = newDuration.filter { it.isDigit() }
                        selectedRecommendedTitle = null
                    },
                    placeholder = "예: 5",
                    digitsOnly = true,
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))

                RecipePrimaryButton(
                    text = "이대로 등록하기",
                    enabled = isRegisterEnabled,
                    onClick = {
                        val parsedDurationMinutes = durationMinutes ?: return@RecipePrimaryButton

                        onRegisterClick(
                            title.trim(),
                            description.trim(),
                            parsedDurationMinutes,
                        )
                    },
                )
            }
        }
    }
}

/**
 * D104 상단 추천 레시피 칩 영역.
 *
 * 현재 추천 문구는 임시 데이터다.
 * 기획 확정 후 RecipeUiState.kt의 sampleRecommendedRecipes만 수정하면 된다.
 */
@Composable
private fun RecommendedRecipeChips(
    recommendedRecipes: List<RecommendedRecipe>,
    selectedTitle: String?,
    onRecommendedRecipeClick: (RecommendedRecipe) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        recommendedRecipes.chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowItems.forEach { recommendedRecipe ->
                    RecipeRecommendChip(
                        text = recommendedRecipe.title,
                        selected = selectedTitle == recommendedRecipe.title,
                        onClick = {
                            onRecommendedRecipeClick(recommendedRecipe)
                        },
                    )
                }
            }
        }
    }
}