package com.gominitta.android.presentation.mypage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gominitta.android.presentation.mypage.model.FavoriteTimeUiModel
import com.gominitta.android.presentation.mypage.model.TimeValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

enum class EditingField { START, END }

@HiltViewModel
class FavoriteTimeAddViewModel @Inject constructor() : ViewModel() {

    var title by mutableStateOf("")
        private set

    var startTime by mutableStateOf(TimeValue(hour = 9, minute = 0, isPm = true))
        private set

    var endTime by mutableStateOf(TimeValue(hour = 10, minute = 0, isPm = true))
        private set

    var editingField by mutableStateOf<EditingField?>(null)
        private set

    fun onTitleChange(newTitle: String) {
        title = newTitle.take(20)
    }

    fun onStartTimeClick() {
        editingField = EditingField.START
    }

    fun onEndTimeClick() {
        editingField = EditingField.END
    }

    fun onTimeChange(updated: TimeValue) {
        when (editingField) {
            EditingField.START -> startTime = updated
            EditingField.END -> endTime = updated
            null -> Unit
        }
    }

    fun onTimePickerDismiss() {
        editingField = null
    }

    fun toUiModel(): FavoriteTimeUiModel = FavoriteTimeUiModel(
        id = System.currentTimeMillis(),
        title = title.trim(),
        startTime = startTime,
        endTime = endTime,
    )
}