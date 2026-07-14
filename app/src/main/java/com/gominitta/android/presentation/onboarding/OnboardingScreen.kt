package com.gominitta.android.presentation.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.components.GominittaButton
import com.gominitta.android.ui.components.GominittaButtonVariant
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Gray200
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray600
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Heading4_18m
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary400
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb
import kotlinx.coroutines.launch

private data class OnboardingPage(val title: String, val subtitle: String)

private val onboardingPages = listOf(
    OnboardingPage(
        "걱정은 잠시 접어두고, 이따가 마주해요",
        "지금 당장 해결할 수 없는 고민에 갇혀 있나요?\n복잡한 생각은 잠시 앱에 맡겨두고 온전히 일상에 집중해 보세요.",
    ),
    OnboardingPage(
        "정해진 시간에 안전하게 꺼내보는 마음",
        "불쑥 찾아오는 생각들을 기록해 보세요.\n약속된 시간이 되면 온전히 내 마음과 마주해요.",
    ),
    OnboardingPage(
        "(3페이지 문구 준비 중)",           // TODO: 문구 확정 시 교체
        "(내부 문구 미정 — PM 확정 후 교체 예정)",
    ),
)

/**
 * 온보딩 화면 — 스플래시 후 최초 진입. 3페이지 가로 페이저(인디케이터·건너뛰기·다음).
 * 마지막 페이지 '다음' 또는 '건너뛰기' → 로그인. 일러스트/3페이지 문구는 플레이스홀더.
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

                    // TODO: 실제 에셋 교체
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(0.64f)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp),
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(Primary200),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("일러스트 준비 중", style = Body3_14r, color = Gray400)
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
