package com.gominitta.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary300
import com.gominitta.android.ui.theme.Primary400
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.White800

@Composable
fun HeatMap(
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    val colors = listOf(
        White800,
        Primary200,
        Primary300,
        Primary400,
        Primary800,
        Primary200,
        Primary200,
    )

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
                        if (index == 0) {
                            Modifier.border(
                                width = 1.dp,
                                color = Primary200,
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
        HeatMap()
    }
}
