package com.gominitta.android.presentation.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.ui.components.GominittaButton
import com.gominitta.android.ui.components.GominittaButtonVariant
import com.gominitta.android.ui.components.GominittaCard
import com.gominitta.android.ui.components.GominittaElevatedCard
import com.gominitta.android.ui.theme.AccentCream100
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading4_18m
import com.gominitta.android.ui.theme.Primary300
import com.gominitta.android.ui.theme.Primary400
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb
import com.gominitta.android.ui.theme.White800

/**
 * 마음 세션 진행 (C102 인트로 바텀시트 + C103 세션 기록 3종). 세션 상세 → 시작.
 * 진입 시 "더 나은 기분으로 시작해볼까요?" 바텀시트가 한 번 뜨고, 아래엔 걱정 기록용
 * 텍스트/음성/사진 3탭이 있다. 지금은 API 연동 전이라 걱정 내용은 하드코딩된 더미값이고,
 * 음성 인식·카메라 텍스트 인식 탭은 아이콘만 있는 자리표시자(실제 녹음/촬영 미구현).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionActiveScreen(
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableStateOf(RecordTab.Text) }
    var noteText by remember { mutableStateOf("") }
    var showIntroSheet by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        SessionActiveContent(
            innerPadding = innerPadding,
            worryTitle = FAKE_WORRY_TITLE,
            worryMemo = FAKE_WORRY_MEMO,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            noteText = noteText,
            onNoteTextChange = { noteText = it },
            onNavigateBack = onNavigateBack,
            onCompleteSession = onNavigateNext,
        )
    }

    if (showIntroSheet) {
        ModalBottomSheet(
            onDismissRequest = { showIntroSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            SessionIntroSheetContent(
                onSkip = { showIntroSheet = false },
                onStartRecipe = {
                    showIntroSheet = false
                    // TODO: 마음 레시피 실행 플로우 연결
                },
            )
        }
    }
}

@Composable
private fun SessionActiveContent(
    innerPadding: PaddingValues,
    worryTitle: String,
    worryMemo: String,
    selectedTab: RecordTab,
    onTabSelected: (RecordTab) -> Unit,
    noteText: String,
    onNoteTextChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onCompleteSession: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .imePadding()
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 24.dp),
    ) {
        Box(Modifier.fillMaxWidth()) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = "뒤로",
                tint = Primary800,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp)
                    .clickable(onClick = onNavigateBack),
            )
            Text(
                text = "마음 세션",
                style = Title1_20sb,
                color = Primary800,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(text = "예약된 걱정", style = Heading4_18m, color = Primary800)
            Spacer(Modifier.height(8.dp))
            GominittaElevatedCard(modifier = Modifier.height(170.dp)) {
                Text(text = worryTitle, style = Body1_16m, color = Primary800)
                Spacer(Modifier.height(4.dp))
                Text(text = worryMemo, style = Body3_14r, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                RecordTab.entries.forEach { tab ->
                    RecordTypeTabButton(
                        tab = tab,
                        selected = tab == selectedTab,
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(Modifier.height(37.dp))

            when (selectedTab) {
                RecordTab.Text -> TextRecordArea(value = noteText, onValueChange = onNoteTextChange)
                RecordTab.Voice -> PlaceholderRecordArea(
                    guide = "지금 드는 생각을 자유롭게 털어놔보세요. 중간중간 마이크를 눌러 멈춰도 돼요. " +
                        "다 끝나면 세션 완료하기를 누르세요.",
                    icon = R.drawable.ic_mic,
                )
                RecordTab.Camera -> PlaceholderRecordArea(
                    guide = "노트나 일기장에 적어둔 내용이 있다면 카메라로 스캔해보세요.",
                    icon = R.drawable.ic_camera,
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        GominittaButton(
            text = "세션 완료하기",
            onClick = onCompleteSession,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private enum class RecordTab(val label: String, val icon: Int) {
    Text("텍스트 입력", R.drawable.ic_textpencil),
    Voice("음성 인식", R.drawable.ic_mic),
    Camera("텍스트 인식", R.drawable.ic_camera),
}

@Composable
private fun RecordTypeTabButton(
    tab: RecordTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint = if (selected) Primary800 else Gray400
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(if (selected) AccentCream100 else White800)
            .then(
                if (selected) Modifier.border(1.dp, Primary800, shape) else Modifier,
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(painter = painterResource(tab.icon), contentDescription = null, tint = Primary400, modifier = Modifier.size(32.dp))
        Spacer(Modifier.height(6.dp))
        Text(text = tab.label, style = Body3_14r, color = tint)
    }
}

val TapeSize = DpSize(96.dp, 28.dp)


@Composable
fun WashiTapeDecoration(modifier: Modifier = Modifier, color: Color = Primary300) {
    Box(
        modifier = modifier
            .size(TapeSize.width, TapeSize.height)
            .background(color),
    )
}

@Composable
private fun TextRecordArea(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        GominittaCard {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = Body2_15r.copy(color = Primary800),
                modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = "지금 드는 생각을 그대로 적어보세요.\n기록하지 않고 완료해도 괜찮아요.",
                            style = Body2_15r,
                            color = Gray400,
                        )
                    }
                    innerTextField()
                },
            )
        }

        WashiTapeDecoration(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = -(TapeSize.height / 2)),
        )
    }
}

@Composable
private fun PlaceholderRecordArea(guide: String, icon: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = guide, style = Body2_15r, color = Gray400)
        Spacer(Modifier.height(48.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(140.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Primary400,
                modifier = Modifier.size(100.dp),
            )
        }
    }
}

@Composable
private fun SessionIntroSheetContent(onSkip: () -> Unit, onStartRecipe: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "더 나은 기분으로 시작해볼까요?",
            style = Title1_20sb,
            color = Primary800,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "안정된 기분으로 고민을 마주하면\n더 차분하게 정리할 수 있어요.",
            style = Body2_15r,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GominittaButton(
                text = "건너뛰기",
                onClick = onSkip,
                modifier = Modifier.weight(1f),
                variant = GominittaButtonVariant.Outlined,
            )
            GominittaButton(
                text = "마음 레시피 실행하기",
                onClick = onStartRecipe,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ---- Fake data (API 연동 전) --------------------------------------------------

private const val FAKE_WORRY_TITLE = "UMC 프론트가 안 구해지면 어떡하지"
private const val FAKE_WORRY_MEMO = "걱정걱정걱정"

// ---- Preview ---------------------------------------------------------------

@Preview(name = "SessionActive - 텍스트 탭", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun SessionActiveContentTextPreview() {
    GominittaTheme {
        SessionActiveContent(
            innerPadding = PaddingValues(0.dp),
            worryTitle = FAKE_WORRY_TITLE,
            worryMemo = FAKE_WORRY_MEMO,
            selectedTab = RecordTab.Text,
            onTabSelected = {},
            noteText = "",
            onNoteTextChange = {},
            onNavigateBack = {},
            onCompleteSession = {},
        )
    }
}

@Preview(name = "SessionActive - 음성 탭", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun SessionActiveContentVoicePreview() {
    GominittaTheme {
        SessionActiveContent(
            innerPadding = PaddingValues(0.dp),
            worryTitle = FAKE_WORRY_TITLE,
            worryMemo = FAKE_WORRY_MEMO,
            selectedTab = RecordTab.Voice,
            onTabSelected = {},
            noteText = "",
            onNoteTextChange = {},
            onNavigateBack = {},
            onCompleteSession = {},
        )
    }
}

@Preview(name = "SessionActive - 인트로 바텀시트", showBackground = true, backgroundColor = 0xFFFEFEFB)
@Composable
private fun SessionIntroSheetContentPreview() {
    GominittaTheme {
        SessionIntroSheetContent(onSkip = {}, onStartRecipe = {})
    }
}
