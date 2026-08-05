package com.gominitta.android.presentation.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gominitta.android.presentation.recipe.components.RecipeInputField
import com.gominitta.android.presentation.recipe.components.RecipePrimaryButton
import com.gominitta.android.presentation.recipe.components.RecipeRecommendChip
import com.gominitta.android.presentation.recipe.components.RecipeScreenScaffold

private const val MIN_DURATION_MINUTES = 1
private const val MAX_DURATION_MINUTES = 60

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
    val displayedRecommendedRecipes = remember(recommendedRecipes) {
        recommendedRecipes.shuffled().take(3) // 변경 — 필터 없이 27개 전체에서 랜덤
    }

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

    var durationErrorMessage by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val isRegisterEnabled =
        title.isNotBlank() && description.isNotBlank()

    fun validateDurationOrShowError(): Int? {
        val minutes = duration.toIntOrNull()
        return when {
            duration.isBlank() -> {
                durationErrorMessage = "예상 소요 시간을 입력해주세요"
                null
            }
            minutes == null || minutes !in MIN_DURATION_MINUTES..MAX_DURATION_MINUTES -> {
                durationErrorMessage =
                    "${MIN_DURATION_MINUTES}~${MAX_DURATION_MINUTES}분 사이의 시간을 입력해주세요"
                null
            }
            else -> {
                durationErrorMessage = null
                minutes
            }
        }
    }

    RecipeScreenScaffold(
        title = "새 레시피 등록",
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
        ) {
            Text(
                text = "이런 레시피는 어때요?",
                fontSize = 15.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.3).sp,
                color = Color(0xFF404040),
            )

            Spacer(modifier = Modifier.height(10.dp))

            RecommendedRecipeChips(
                recommendedRecipes = displayedRecommendedRecipes,
                selectedTitle = selectedRecommendedTitle,
                onRecommendedRecipeClick = { recommendedRecipe ->
                    selectedRecommendedTitle = recommendedRecipe.title
                    title = recommendedRecipe.title
                    description = recommendedRecipe.description
                    duration = recommendedRecipe.durationMinutes.toString()
                    durationErrorMessage = null
                },
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "직접 입력하기",
                fontSize = 20.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.4).sp,
                color = Color(0xFF404040),
            )

            Spacer(modifier = Modifier.height(14.dp))

            RecipeInputField(
                label = "레시피 명",
                value = title,
                onValueChange = { newTitle ->
                    title = newTitle
                    selectedRecommendedTitle = null
                },
                placeholder = "예: 따뜻한 차 마시기",
            )

            Spacer(modifier = Modifier.height(14.dp))

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

            Spacer(modifier = Modifier.height(14.dp))

            RecipeInputField(
                label = "예상 소요 시간",
                value = duration,
                onValueChange = { newDuration ->
                    duration = newDuration.filter(Char::isDigit)
                    selectedRecommendedTitle = null
                    durationErrorMessage = null
                },
                placeholder = "1~60분 사이로 입력해주세요",
                digitsOnly = true,
                errorMessage = durationErrorMessage,
            )

            Spacer(modifier = Modifier.height(24.dp))

            RecipePrimaryButton(
                text = "이대로 등록하기",
                enabled = isRegisterEnabled,
                onClick = {
                    val parsedDurationMinutes =
                        validateDurationOrShowError() ?: return@RecipePrimaryButton

                    onRegisterClick(
                        title.trim(),
                        description.trim(),
                        parsedDurationMinutes,
                    )
                },
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun RecommendedRecipeChips(
    recommendedRecipes: List<RecommendedRecipe>,
    selectedTitle: String?,
    onRecommendedRecipeClick: (RecommendedRecipe) -> Unit,
) {
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        recommendedRecipes.forEach { recommendedRecipe ->
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