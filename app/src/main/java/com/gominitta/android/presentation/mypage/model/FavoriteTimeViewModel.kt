package com.gominitta.android.presentation.mypage.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteTimeViewModel @Inject constructor(
    private val repository: MyPageRepository,
) : ViewModel() {

    val favoriteTimes = mutableStateListOf<FavoriteTimeUiModel>().apply {
        addAll(repository.getFavoriteTimes())
    }

    fun add(item: FavoriteTimeUiModel) {
        favoriteTimes.add(item)
    }

    fun update(item: FavoriteTimeUiModel) {
        val index = favoriteTimes.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            favoriteTimes[index] = item
        }
    }

    fun remove(item: FavoriteTimeUiModel) {
        favoriteTimes.removeAll { it.id == item.id }
    }
}