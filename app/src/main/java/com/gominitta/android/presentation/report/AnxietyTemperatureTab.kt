package com.gominitta.android.presentation.report

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gominitta.android.ui.components.HeatMap

@Composable
internal fun AnxietyTemperatureTab(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        HeatMap()
    }
}
