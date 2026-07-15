package com.gominitta.android.presentation.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gominitta.android.presentation.recipe.components.RecipeEditTopBar
import com.gominitta.android.presentation.recipe.components.RecipeInputField
import com.gominitta.android.presentation.recipe.components.RecipePrimaryButton

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

    val durationMinutes = duration.toIntOrNull()

    val isCompleteEnabled = title.isNotBlank() &&
            description.isNotBlank() &&
            durationMinutes != null &&
            durationMinutes > 0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                RecipeEditTopBar(
                    title = "레시피 수정",
                    onBackClick = onNavigateBack,
                    onDeleteClick = {
                        onDeleteClick(recipe.id)
                    },
                )

                Spacer(modifier = Modifier.height(8.dp))

                RecipeInputField(
                    label = "레시피 명",
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "레시피 명을 입력하세요",
                )

                RecipeInputField(
                    label = "수행 방법",
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "수행 방법을 입력하세요",
                    singleLine = false,
                    minLines = 4,
                )

                RecipeInputField(
                    label = "예상 소요 시간",
                    value = duration,
                    onValueChange = { duration = it },
                    placeholder = "예: 5",
                    digitsOnly = true,
                )
            }

            RecipePrimaryButton(
                text = "수정 완료하기",
                enabled = isCompleteEnabled,
                onClick = {
                    val parsedDurationMinutes = durationMinutes ?: return@RecipePrimaryButton

                    onCompleteClick(
                        recipe.id,
                        title.trim(),
                        description.trim(),
                        parsedDurationMinutes,
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 28.dp),
            )
        }
    }
}