package com.gominitta.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.theme.AccentCream100
import com.gominitta.android.ui.theme.AccentCream300
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading1_24sb
import com.gominitta.android.ui.theme.Heading3_20m
import com.gominitta.android.ui.theme.Heading4_18m
import com.gominitta.android.ui.theme.Heading5_15m
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary400
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.White800

/**
 * 데이터 값에 따라 디자인 가이드의 대/중/소 버블을 선택합니다.
 * 실제 API의 구간 정책이 확정되면 이 함수의 기준값만 교체하면 됩니다.
 */
@Composable
fun WorryMapBubble(
    title: String,
    value: Int,
    modifier: Modifier = Modifier,
    mediumBackgroundColor: Color = Primary400,
) {
    when {
        value >= 50 -> WorryMapLargeBubble(
            title = title,
            percentage = "$value%",
            modifier = modifier,
        )
        value >= 25 -> WorryMapMediumBubble(
            title = title,
            percentage = "$value%",
            backgroundColor = mediumBackgroundColor,
            modifier = modifier,
        )
        else -> WorryMapSmallBubble(
            title = title,
            percentage = "$value%",
            modifier = modifier,
        )
    }
}

/** WorryMap의 1번 원과 동일한 128dp 강조 버블입니다. */
@Composable
fun WorryMapLargeBubble(
    title: String,
    percentage: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(128.dp)
            .background(
                color = Primary800,
                shape = RoundedCornerShape(88.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.size(width = 42.dp, height = 59.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(width = 42.dp, height = 34.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title,
                    color = White800,
                    style = Heading1_24sb,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }

            Box(
                modifier = Modifier.size(width = 42.dp, height = 25.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = percentage,
                    color = White800,
                    style = Heading4_18m,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}

/** WorryMap의 2·3·4번 원과 동일한 92dp 중간 버블입니다. */
@Composable
fun WorryMapMediumBubble(
    title: String,
    percentage: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(92.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(88.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.size(width = 42.dp, height = 49.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(width = 42.dp, height = 28.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title,
                    color = Primary800,
                    style = Heading3_20m,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }

            Box(
                modifier = Modifier.size(width = 42.dp, height = 21.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = percentage,
                    color = Primary800,
                    style = Heading5_15m,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}

/** WorryMap의 5번 원과 동일한 64dp 작은 버블입니다. */
@Composable
fun WorryMapSmallBubble(
    title: String,
    percentage: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .background(
                color = Primary200,
                shape = RoundedCornerShape(88.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.size(width = 42.dp, height = 45.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(width = 42.dp, height = 25.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title,
                    color = Primary800,
                    style = Heading4_18m,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }

            Box(
                modifier = Modifier.size(width = 42.dp, height = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = percentage,
                    color = Primary800,
                    style = Body3_14r,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun WorryMap(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(564.dp)
            .height(168.dp),
    ) {
        // 첫 번째 원: 진로 70% (128dp)
        WorryMapLargeBubble(
            title = "진로",
            percentage = "70%",
            modifier = Modifier.offset(x = 20.dp, y = 20.dp),
        )

        // 두 번째 원: 학업 40% / Primary400 (92dp)
        Box(
            modifier = Modifier
                .offset(x = 162.dp, y = 38.dp)
                .size(92.dp)
                .background(
                    color = Primary400,
                    shape = RoundedCornerShape(88.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.size(width = 42.dp, height = 49.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.size(width = 42.dp, height = 28.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "학업",
                        color = Primary800,
                        style = Heading3_20m,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }

                Box(
                    modifier = Modifier.size(width = 42.dp, height = 21.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "40%",
                        color = Primary800,
                        style = Heading5_15m,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }

        // 세 번째 원: 학업 40% / AccentCream300 (92dp)
        Box(
            modifier = Modifier
                .offset(x = 268.dp, y = 38.dp)
                .size(92.dp)
                .background(
                    color = AccentCream300,
                    shape = RoundedCornerShape(88.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.size(width = 42.dp, height = 49.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.size(width = 42.dp, height = 28.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "학업",
                        color = Primary800,
                        style = Heading3_20m,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }

                Box(
                    modifier = Modifier.size(width = 42.dp, height = 21.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "40%",
                        color = Primary800,
                        style = Heading5_15m,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }

        // 네 번째 원: 취업 40% / AccentCream100 (92dp)
        Box(
            modifier = Modifier
                .offset(x = 374.dp, y = 38.dp)
                .size(92.dp)
                .background(
                    color = AccentCream100,
                    shape = RoundedCornerShape(88.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.size(width = 42.dp, height = 49.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.size(width = 42.dp, height = 28.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "취업",
                        color = Primary800,
                        style = Heading3_20m,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }

                Box(
                    modifier = Modifier.size(width = 42.dp, height = 21.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "40%",
                        color = Primary800,
                        style = Heading5_15m,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }

        // 다섯 번째 원: 가족 10% (64dp)
        Box(
            modifier = Modifier
                .offset(x = 480.dp, y = 52.dp)
                .size(64.dp)
                .background(
                    color = Primary200,
                    shape = RoundedCornerShape(88.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.size(width = 42.dp, height = 45.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.size(width = 42.dp, height = 25.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "가족",
                        color = Primary800,
                        style = Heading4_18m,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }

                Box(
                    modifier = Modifier.size(width = 42.dp, height = 20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "10%",
                        color = Primary800,
                        style = Body3_14r,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Worry map - largest bubble",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 580,
    heightDp = 184,
)
@Composable
private fun WorryMapPreview() {
    GominittaTheme {
        WorryMap()
    }
}
