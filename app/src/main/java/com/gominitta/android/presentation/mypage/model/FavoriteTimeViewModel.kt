package com.gominitta.android.presentation.mypage.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.model.mypage.FavoriteTime
import com.gominitta.android.domain.usecase.AddFavoriteTimeUseCase
import com.gominitta.android.domain.usecase.DeleteFavoriteTimeUseCase
import com.gominitta.android.domain.usecase.GetFavoriteTimesUseCase
import com.gominitta.android.domain.usecase.UpdateFavoriteTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteTimeViewModel @Inject constructor(
    private val getFavoriteTimesUseCase: GetFavoriteTimesUseCase,
    private val addFavoriteTimeUseCase: AddFavoriteTimeUseCase,
    private val updateFavoriteTimeUseCase: UpdateFavoriteTimeUseCase,
    private val deleteFavoriteTimeUseCase: DeleteFavoriteTimeUseCase,
) : ViewModel() {

    var favoriteTimes by mutableStateOf<List<FavoriteTimeUiModel>>(emptyList())
        private set

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            favoriteTimes = getFavoriteTimesUseCase().map { it.toUiModel() }
        }
    }

    fun add(item: FavoriteTimeUiModel) {
        viewModelScope.launch {
            addFavoriteTimeUseCase(item.title, item.startTime.toLocalTime(), item.endTime.toLocalTime())
            refresh()
        }
    }

    fun update(item: FavoriteTimeUiModel) {
        viewModelScope.launch {
            updateFavoriteTimeUseCase(item.toDomain())
            refresh()
        }
    }

    fun remove(item: FavoriteTimeUiModel) {
        viewModelScope.launch {
            deleteFavoriteTimeUseCase(item.id)
            refresh()
        }
    }
}