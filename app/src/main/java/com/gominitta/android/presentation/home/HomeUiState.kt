package com.gominitta.android.presentation.home

import com.gominitta.android.domain.model.NextSession

data class HomeUiState(
    val nickname: String = "",
    val dailyMessage: String = "",
    val nextSession: NextSession? = null,
    val profileImageUrl: String = "",
)
