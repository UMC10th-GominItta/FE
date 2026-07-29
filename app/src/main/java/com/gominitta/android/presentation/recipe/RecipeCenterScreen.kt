package com.gominitta.android.presentation.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gominitta.android.presentation.recipe.components.RecipeAddFloatingButton
import com.gominitta.android.presentation.recipe.components.RecipeCard
import com.gominitta.android.presentation.recipe.components.RecipeScreenScaffold

@Composable
fun RecipeCenterScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    recipes: List<RecipeItem> = emptyList(),
    onRecipeClick: (Long) -> Unit = {},
    onEditClick: (Long) -> Unit = {},
    onDeleteClick: (Long) -> Unit = {},
    onCreateClick: () -> Unit = {},
) {
    RecipeScreenScaffold(
        title = "마음 레시피 센터",
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 26.dp,
                    bottom = 112.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Text(
                        text = "지금 나에게 맞는 레시피를 선택해 보세요.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        lineHeight = 25.2.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = (-0.36).sp,
                        color = Color(0xFF404040),
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (recipes.isEmpty()) {
                    item {
                        RecipeEmptyState(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 132.dp),
                        )
                    }
                } else {
                    items(
                        items = recipes,
                        key = { recipe ->
                            recipe.id
                        },
                    ) { recipe ->
                        RecipeCard(
                            title = recipe.title,
                            durationMinutes = recipe.durationMinutes,
                            onClick = {
                                onRecipeClick(recipe.id)
                            },
                            onEditClick = {
                                onEditClick(recipe.id)
                            },
                            onDeleteClick = {
                                onDeleteClick(recipe.id)
                            },
                        )
                    }
                }
            }

            RecipeAddFloatingButton(
                onClick = onCreateClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 24.dp,
                        bottom = 32.dp,
                    ),
            )
        }
    }
}

@Composable
private fun RecipeEmptyState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "레시피가 없습니다.",
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-0.28).sp,
            textAlign = TextAlign.Center,
            color = Color(0xFFA6A6A6),
        )
    }
}