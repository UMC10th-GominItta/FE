package com.gominitta.android.presentation.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gominitta.android.R
import com.gominitta.android.presentation.recipe.components.RecipeInputField
import com.gominitta.android.presentation.recipe.components.RecipePrimaryButton
import com.gominitta.android.presentation.recipe.components.RecipeScreenScaffold

@Composable
fun RecipeEditScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeEditViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel.isDeleted, viewModel.isUpdated) {
        if (viewModel.isDeleted || viewModel.isUpdated) {
            onNavigateBack()
        }
    }

    RecipeScreenScaffold(
        title = "레시피 수정",
        onNavigateBack = onNavigateBack,
        modifier = modifier,
        topBarAction = {
            IconButton(
                onClick = viewModel::onDeleteClick,
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_recipe_trash),
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
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                return@RecipeScreenScaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 22.dp, bottom = 72.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                RecipeInputField(
                    label = "레시피 명",
                    value = viewModel.title,
                    onValueChange = viewModel::onTitleChange,
                    placeholder = "레시피 명을 입력하세요",
                )

                RecipeInputField(
                    label = "수행 방법",
                    value = viewModel.description,
                    onValueChange = viewModel::onDescriptionChange,
                    placeholder = "수행 방법을 입력하세요",
                    singleLine = false,
                    minLines = 4,
                )

                RecipeInputField(
                    label = "예상 소요 시간",
                    value = viewModel.duration,
                    onValueChange = viewModel::onDurationChange,
                    placeholder = "1~60분 사이로 입력해주세요",
                    digitsOnly = true,
                    errorMessage = viewModel.durationErrorMessage,
                )
            }

            RecipePrimaryButton(
                text = "수정 완료하기",
                enabled = viewModel.isCompleteEnabled,
                onClick = viewModel::onCompleteClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
            )
        }
    }
}