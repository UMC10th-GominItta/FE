package com.gominitta.android.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.primary300Token
import kotlin.math.ceil

@Composable
fun GominittaHeatMap(
    modifier: Modifier = Modifier,
    frequencies: List<Int> = listOf(0, 1, 2, 3, 4, 1, 1),
    maxFrequency: Int = frequencies.maxOrNull() ?: 0,
) {
    val shape = MaterialTheme.shapes.small
    val levels = frequencies.take(7).map { frequency ->
        if (frequency <= 0 || maxFrequency <= 0) {
            0
        } else {
            ceil(frequency.toDouble() / maxFrequency * 4)
                .toInt()
                .coerceIn(1, 4)
        }
    }
    val colors = levels.map { level ->
        when (level.coerceIn(0, 4)) {
            0 -> MaterialTheme.colorScheme.surface
            1 -> MaterialTheme.colorScheme.secondaryContainer
            2 -> MaterialTheme.colorScheme.primary300Token
            3 -> MaterialTheme.colorScheme.outline
            else -> MaterialTheme.colorScheme.onSecondary
        }
    }

    Row(
        modifier = modifier.size(width = 227.dp, height = 44.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        colors.forEachIndexed { index, color ->
            Box(
                modifier = Modifier
                    .size(width = 29.dp, height = 44.dp)
                    .background(color = color, shape = shape)
                    .then(
                        if (levels.getOrNull(index) == 0) {
                            Modifier.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = shape,
                            )
                        } else {
                            Modifier
                        },
                    ),
            )
        }
    }
}

@Preview(
    name = "Heat map",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 243,
    heightDp = 60,
)
@Composable
private fun HeatMapPreview() {
    GominittaTheme {
        GominittaHeatMap()
    }
}
