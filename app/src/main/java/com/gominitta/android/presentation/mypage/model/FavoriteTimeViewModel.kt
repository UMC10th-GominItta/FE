package com.gominitta.android.presentation.mypage.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.gominitta.android.presentation.mypage.model.FavoriteTimeUiModel

class FavoriteTimeViewModel : ViewModel() {

    val favoriteTimes = mutableStateListOf<FavoriteTimeUiModel>()

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