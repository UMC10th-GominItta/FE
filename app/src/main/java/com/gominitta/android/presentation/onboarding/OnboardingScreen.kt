package com.gominitta.android.presentation.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.ui.components.GominittaButton
import com.gominitta.android.ui.components.GominittaButtonVariant
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Gray200
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Heading4_18m
import com.gominitta.android.ui.theme.Primary800
import kotlinx.coroutines.launch

private data class OnboardingPage(val title: String, val subtitle: String, @DrawableRes val illustration: Int)

private val onboardingPages = listOf(
    OnboardingPage(
        "걱정은 잠시 접어두고, 이따가 마주해요",
        "바쁜 일상 속 문득 떠오르는 고민,\n꺼내볼 시간을 정한 뒤 온전히 현재의 일상에 집중해 보세요.",
        R.drawable.onboarding_1,
    ),
    OnboardingPage(
        "여유가 생긴 지금, 깊이 마주하기",
        "약속했던 시간이 되면, 바쁜 일상에서 한 걸음 벗어나\n충분한 여유 속에서 내 마음을 차분히 마주해보세요.",
        R.drawable.onboarding_2,
    ),
    OnboardingPage(
        "언제든 꺼내보는 나만의 리프레시 비법",
        "좋아하는 것들을 모아 나만의 레시피를 만들어 보세요.\n틈틈이 챙기는 여유들이 모여 나를 돌보는 힘이 돼요.",
        R.drawable.onboarding_3,
    ),
    OnboardingPage(
        "데이터로 보는 지나온 마음의 지도",
        "쌓여가는 고민 속에서 나만의 마음 패턴을 들여다보면,\n어느새 나를 더 깊이 이해하게 될 거예요.",
        R.drawable.onboarding_4,
    ),
)

/**
 * 온보딩 화면 — 스플래시 후 최초 진입. 4페이지 가로 페이저(인디케이터·건너뛰기·다음).
 * 마지막 페이지 '다음' 또는 '건너뛰기' → 로그인.
 */
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
        ) {
            val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
            val scope = rememberCoroutineScope()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
            ) {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(onboardingPages.size) { i ->
                        val active = i == pagerState.currentPage
                        val width by animateDpAsState(if (active) 32.dp else 14.dp, label = "indicator")
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (active) Primary800 else Gray200),
                        )
                    }
                }

                Text(
                    text = "건너뛰기",
                    style = Body2_15r,
                    color = Gray400,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(onClick = onNavigateToLogin),
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = onboardingPages[page].title,
                        style = Heading4_18m,
                        color = Gray800,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = onboardingPages[page].subtitle,
                        style = Body3_14r,
                        color = Gray800,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(32.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(onboardingPages[page].illustration),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(375f / 812f, matchHeightConstraintsFirst = true)
                                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp)),
                        )
                    }

                    Spacer(Modifier.height(48.dp))
                }
            }

            GominittaButton(
                text = "다음",
                onClick = {
                    if (pagerState.currentPage < onboardingPages.lastIndex) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onNavigateToLogin()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                variant = GominittaButtonVariant.Primary,
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}
