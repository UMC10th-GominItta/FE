package com.gominitta.android.presentation.worry.components

import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Button1_15m
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Heading4_18m
import com.gominitta.android.ui.theme.White800

private val DialogBackground = Color(0xFFF8F8F8)
private val DialogDivider = Color(0xFF808080)
private val DialogTitleColor = Color(0xFF242424)
private val DialogBodyColor = Color(0xFF808080)
private val DialogShadowColor = Color(0xFF000000)

/**
 * 걱정 예약 플로우 공용 작성 취소 확인 다이얼로그 — 화면에 종속되지 않는 고정 문구.
 * Material3 [androidx.compose.material3.AlertDialog]는 버튼이 우하단에 뭉치므로,
 * 좌우 반반 분할 레이아웃을 위해 [Dialog]로 직접 구성한다.
 */
@Composable
fun WorryExitDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val view = LocalView.current
        val window = (view.parent as? DialogWindowProvider)?.window

        LaunchedEffect(window) {
            val w = window ?: return@LaunchedEffect
            // 플랫폼 기본 dim(검정) 제거 — 스크림은 아래에서 직접 그린다.
            w.setDimAmount(0f)

            // 배경 흐림은 Android 12+ 에서, 그리고 기기가 크로스 윈도우 블러를 지원할 때만 적용된다.
            // (저사양 기기·배터리 세이버·접근성 '투명도 줄이기' 에서는 꺼진다 → 스크림만 남는다)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val wm = view.context.getSystemService(WindowManager::class.java)
                if (wm?.isCrossWindowBlurEnabled == true) {
                    w.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                    w.attributes = w.attributes.apply { blurBehindRadius = 8 }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White800.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onDismiss() },
            contentAlignment = Alignment.Center,
        ) {
            // clip 과 shadow 가 어긋나지 않도록 shape 는 하나만 두고 공유한다.
            val dialogShape = RoundedCornerShape(14.dp)

            Surface(
                modifier = Modifier
                    .width(270.dp)
                    // Surface 의 shadowElevation 은 색을 못 바꾸므로 Modifier.shadow 로 그린다.
                    .shadow(
                        elevation = 12.dp,
                        shape = dialogShape,
                        clip = false,
                        ambientColor = DialogShadowColor,
                        spotColor = DialogShadowColor,
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { /* 다이얼로그 내부 탭이 스크림으로 새어나가 닫히는 것을 막는다 */ },
                shape = dialogShape,
                color = DialogBackground,
            ) {
                Column {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "작성을 취소하시겠습니까?",
                            style = Heading4_18m,
                            color = DialogTitleColor,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "작성 중인 내용이 저장되지 않습니다.",
                            style = Body3_14r,
                            color = DialogBodyColor,
                            textAlign = TextAlign.Center,
                        )
                    }

                    HorizontalDivider(thickness = 0.33.dp, color = DialogDivider)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(onClick = onDismiss),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "취소", style = Button1_15m, color = Gray800)
                        }

                        VerticalDivider(thickness = 0.33.dp, color = DialogDivider)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(onClick = onConfirm),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "확인", style = Button1_15m, color = Gray800)
                        }
                    }
                }
            }
        }
    }
}
