package com.gominitta.android.presentation.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.presentation.recipe.components.RecipeInputField
import com.gominitta.android.presentation.recipe.components.RecipePrimaryButton
import com.gominitta.android.presentation.recipe.components.RecipeScreenScaffold

private const val MIN_DURATION_MINUTES = 1
private const val MAX_DURATION_MINUTES = 60

@Composable
fun RecipeEditScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    recipe: RecipeItem = sampleRecipes.first(),
    onDeleteClick: (Long) -> Unit = {},
    onCompleteClick: (
        recipeId: Long,
        title: String,
        description: String,
        durationMinutes: Int,
    ) -> Unit = { _, _, _, _ -> },
) {
    var title by rememberSaveable(recipe.id) {
        mutableStateOf(recipe.title)
    }

    var description by rememberSaveable(recipe.id) {
        mutableStateOf(recipe.description)
    }

    var duration by rememberSaveable(recipe.id) {
        mutableStateOf(recipe.durationMinutes.toString())
    }

    var durationErrorMessage by rememberSaveable(recipe.id) {
        mutableStateOf<String?>(null)
    }

    val isCompleteEnabled =
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
        title = "레시피 수정",
        onNavigateBack = onNavigateBack,
        modifier = modifier,
        topBarAction = {
            IconButton(
                onClick = {
                    onDeleteClick(recipe.id)
                },
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_recipe_trash,
                    ),
                    contentDescription = "레시피 삭제",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black,
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        top = 22.dp,
                        bottom = 72.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                RecipeInputField(
                    label = "레시피 명",
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    placeholder = "레시피 명을 입력하세요",
                )

                RecipeInputField(
                    label = "수행 방법",
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    placeholder = "수행 방법을 입력하세요",
                    singleLine = false,
                    minLines = 4,
                )

                RecipeInputField(
                    label = "예상 소요 시간",
                    value = duration,
                    onValueChange = {
                        duration = it.filter(Char::isDigit)
                        durationErrorMessage = null
                    },
                    placeholder = "1~60분 사이로 입력해주세요",
                    digitsOnly = true,
                    errorMessage = durationErrorMessage,
                )
            }

            RecipePrimaryButton(
                text = "수정 완료하기",
                enabled = isCompleteEnabled,
                onClick = {
                    val parsedDurationMinutes =
                        validateDurationOrShowError() ?: return@RecipePrimaryButton

                    onCompleteClick(
                        recipe.id,
                        title.trim(),
                        description.trim(),
                        parsedDurationMinutes,
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
            )
        }
    }
}