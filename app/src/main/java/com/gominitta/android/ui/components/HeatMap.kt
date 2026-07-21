package com.gominitta.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
    Box(
        modifier = modifier
            .width(201.dp)
            .height(84.dp),
    ) {
        // 첫 번째 단계
        Box(
            modifier = Modifier
                .offset(x = 20.dp, y = 20.dp)
                .size(width = 29.dp, height = 44.dp)
                .background(
                    color = White800,
                    shape = RoundedCornerShape(8.dp),
                )
                .border(
                    width = 1.dp,
                    color = Primary200,
                    shape = RoundedCornerShape(8.dp),
                ),
        )

        // 두 번째 단계
        Box(
            modifier = Modifier
                .offset(x = 53.dp, y = 20.dp)
                .size(width = 29.dp, height = 44.dp)
                .background(
                    color = Primary200,
                    shape = RoundedCornerShape(8.dp),
                ),
        )

        // 세 번째 단계
        Box(
            modifier = Modifier
                .offset(x = 86.dp, y = 20.dp)
                .size(width = 29.dp, height = 44.dp)
                .background(
                    color = Primary300,
                    shape = RoundedCornerShape(8.dp),
                ),
        )

        // 네 번째 단계
        Box(
            modifier = Modifier
                .offset(x = 119.dp, y = 20.dp)
                .size(width = 29.dp, height = 44.dp)
                .background(
                    color = Primary400,
                    shape = RoundedCornerShape(8.dp),
                ),
        )

        // 다섯 번째 단계
        Box(
            modifier = Modifier
                .offset(x = 152.dp, y = 20.dp)
                .size(width = 29.dp, height = 44.dp)
                .background(
                    color = Primary800,
                    shape = RoundedCornerShape(8.dp),
                ),
        )
    }
}

@Preview(
    name = "Heat map",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 217,
    heightDp = 100,
)
@Composable
private fun HeatMapPreview() {
    GominittaTheme {
        HeatMap()
    }
}
