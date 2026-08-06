package com.gominitta.android.presentation.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gominitta.android.presentation.recipe.components.RecipeInputField
import com.gominitta.android.presentation.recipe.components.RecipePrimaryButton
import com.gominitta.android.presentation.recipe.components.RecipeRecommendChip
import com.gominitta.android.presentation.recipe.components.RecipeScreenScaffold

@Composable
fun RecipeCreateScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    recommendedRecipes: List<RecommendedRecipe> = sampleRecommendedRecipes,
    viewModel: RecipeCreateViewModel = hiltViewModel(),
) {
    val displayedRecommendedRecipes = remember(recommendedRecipes) {
        recommendedRecipes.shuffled().take(3)
    }

    // 등록 성공하면 ViewModel이 isRegistered = true로 신호 보냄 → 여기서 감지해서 뒤로가기
    LaunchedEffect(viewModel.isRegistered) {
        if (viewModel.isRegistered) {
            onNavigateBack()
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
                selectedTitle = viewModel.selectedRecommendedTitle,
                onRecommendedRecipeClick = viewModel::onRecommendedRecipeSelected,
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
                value = viewModel.title,
                onValueChange = viewModel::onTitleChange,
                placeholder = "예: 따뜻한 차 마시기",
            )

            Spacer(modifier = Modifier.height(14.dp))

            RecipeInputField(
                label = "수행 방법",
                value = viewModel.description,
                onValueChange = viewModel::onDescriptionChange,
                placeholder = "어떻게 불안을 해소할까요?",
                singleLine = false,
                minLines = 4,
            )

            Spacer(modifier = Modifier.height(14.dp))

            RecipeInputField(
                label = "예상 소요 시간",
                value = viewModel.duration,
                onValueChange = viewModel::onDurationChange,
                placeholder = "1~60분 사이로 입력해주세요",
                digitsOnly = true,
                errorMessage = viewModel.durationErrorMessage,
            )

            Spacer(modifier = Modifier.height(24.dp))

            RecipePrimaryButton(
                text = "이대로 등록하기",
                enabled = viewModel.isRegisterEnabled,
                onClick = viewModel::onRegisterClick,
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