package com.gominitta.android.presentation.session

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading4_18m
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary300
import com.gominitta.android.ui.theme.Primary400
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb
import com.gominitta.android.ui.theme.White800

/**
 * 마음 세션 진행 (C102 인트로 바텀시트 + C103 세션 기록 3종). 세션 상세 → 시작.
 * 진입 시 "더 나은 기분으로 시작해볼까요?" 바텀시트가 한 번 뜨고, 아래엔 걱정 기록용
 * 텍스트/음성/사진 3탭이 있다. [sessionId]로 예약된 걱정 내용을 실제로 불러온다.
 * 음성 인식은 온디바이스 [android.speech.SpeechRecognizer]로(마이크 눌러 시작, 다시 눌러 종료),
 * 카메라 텍스트 인식은 시스템 카메라로 촬영한 사진을 ML Kit 온디바이스 한국어 인식기로 처리한다.
 * 둘 다 인식 결과는 noteText 하나로 모여서 세션 완료 → 상세 확인 화면으로 넘어간다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionActiveScreen(
    sessionId: Long,
    onNavigateNext: (recordedText: String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToRecipeCenter: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionActiveViewModel = hiltViewModel(),
) {
    var selectedTab by remember { mutableStateOf(RecordTab.Text) }
    var showIntroSheet by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(sessionId) { viewModel.load(sessionId) }

    // 다른 탭으로 넘어가면 마이크를 켜둔 채로 두지 않는다.
    LaunchedEffect(selectedTab) {
        if (selectedTab != RecordTab.Voice && viewModel.isListening) {
            viewModel.toggleVoiceRecognition(context)
        }
    }

    // 화면을 벗어날 때도 마찬가지로 마이크를 정리한다.
    DisposableEffect(Unit) {
        onDispose {
            if (viewModel.isListening) viewModel.toggleVoiceRecognition(context)
        }
    }

    val requestMicPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) viewModel.toggleVoiceRecognition(context)
    }
    val onMicClick = {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) {
            viewModel.toggleVoiceRecognition(context)
        } else {
            requestMicPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // 시스템 카메라 앱에 위임 — 우리 앱은 CAMERA 권한/미리보기 없이 썸네일 Bitmap만 돌려받는다.
    val takePicture = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview(),
    ) { bitmap ->
        if (bitmap != null) viewModel.recognizeImage(bitmap)
    }
    val onCameraClick = { takePicture.launch(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ) { innerPadding ->
            SessionActiveContent(
                innerPadding = innerPadding,
                worryTitle = viewModel.worryTitle,
                worryMemo = viewModel.worryMemo,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                noteText = viewModel.noteText,
                onNoteTextChange = { viewModel.noteText = it },
                isListening = viewModel.isListening,
                onMicClick = onMicClick,
                isRecognizingImage = viewModel.isRecognizingImage,
                onCameraClick = onCameraClick,
                onNavigateBack = onNavigateBack,
                onCompleteSession = { onNavigateNext(viewModel.noteText) },
            )
        }

        if (showIntroSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
                    .background(SheetScrimGradient),
            )
        }
    }

    if (showIntroSheet) {
        ModalBottomSheet(
            onDismissRequest = { showIntroSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Primary200,
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
            scrimColor = Color.Transparent,
            dragHandle = { BottomSheetDefaults.DragHandle(width = 50.dp) },
        ) {
            SessionIntroSheetContent(
                onSkip = { showIntroSheet = false },
                onStartRecipe = {
                    showIntroSheet = false
                    onNavigateToRecipeCenter()
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
    isListening: Boolean,
    onMicClick: () -> Unit,
    isRecognizingImage: Boolean,
    onCameraClick: () -> Unit,
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
                RecordTab.Voice -> VoiceRecordArea(isListening = isListening, onMicClick = onMicClick)
                RecordTab.Camera -> CameraRecordArea(isRecognizing = isRecognizingImage, onCameraClick = onCameraClick)
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

val TapeSize = DpSize(84.dp, 34.dp)

/** 바텀시트 뜰 때 뒤 화면에 까는 배경 그라데이션 (Figma: 위 투명 → 아래 #FEFDF7). */
val SheetScrimGradient = Brush.verticalGradient(
    0f to Color(0xFFFEFDF7).copy(alpha = 0f),
    1f to Color(0xFFFEFDF7),
)


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
private fun VoiceRecordArea(isListening: Boolean, onMicClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (isListening) {
                "듣고 있어요. 다 말했으면 마이크를 다시 눌러주세요."
            } else {
                "마이크를 누르고 지금 드는 생각을 자유롭게 말해보세요."
            },
            style = Body2_15r,
            color = Gray400,
        )
        Spacer(Modifier.height(48.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(140.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(126.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onMicClick),
                contentAlignment = Alignment.Center,
            ) {
                if (isListening) {
                    MicListeningPulse()
                }
                Icon(
                    painter = painterResource(R.drawable.ic_mic),
                    contentDescription = if (isListening) "녹음 종료" else "녹음 시작",
                    tint = if (isListening) Primary800 else Primary400,
                    modifier = Modifier.size(if (isListening) 56.dp else 100.dp),
                )
            }
        }
    }
}

/** 녹음 중 마이크 주변에 뜨는 방사형 그라데이션(중앙 White800 → 외곽 Primary300) 펄스 애니메이션. */
@Composable
private fun MicListeningPulse(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "micListeningPulse")
    val scale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "micListeningPulseScale",
    )
    Box(
        modifier = modifier
            .size(126.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Brush.radialGradient(colors = listOf(White800, Primary300))),
    )
}

@Composable
private fun CameraRecordArea(isRecognizing: Boolean, onCameraClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (isRecognizing) {
                "사진에서 글자를 읽고 있어요..."
            } else {
                "노트나 일기장에 적어둔 내용이 있다면 카메라로 스캔해보세요."
            },
            style = Body2_15r,
            color = Gray400,
        )
        Spacer(Modifier.height(48.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(enabled = !isRecognizing, onClick = onCameraClick),
            contentAlignment = Alignment.Center,
        ) {
            if (isRecognizing) {
                CircularProgressIndicator(color = Primary800)
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = "사진 찍고 텍스트 인식하기",
                    tint = Primary400,
                    modifier = Modifier.size(100.dp),
                )
            }
        }
    }
}

@Composable
private fun SessionIntroSheetContent(onSkip: () -> Unit, onStartRecipe: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(235.dp) // 283 - 드래그 핸들 영역(48dp)
            .padding(horizontal = 20.dp)
            .padding(bottom = 44.dp, top = 8.dp), // 드래그 핸들(48) + 8 = 맨 위에서 56dp
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "더 나은 기분으로 시작해볼까요?",
            style = Title1_20sb,
            color = Gray800,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "안정된 기분으로 고민을 마주하면\n더 차분하게 정리할 수 있어요.",
            style = Body2_15r,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            GominittaButton(
                text = "건너뛰기",
                onClick = onSkip,
                modifier = Modifier.width(112.dp),
                variant = GominittaButtonVariant.Outlined,
            )
            GominittaButton(
                text = "마음 레시피 실행하기",
                onClick = onStartRecipe,
                modifier = Modifier.width(214.dp),
            )
        }
    }
}

// ---- Preview ---------------------------------------------------------------

@Preview(name = "SessionActive - 텍스트 탭", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun SessionActiveContentTextPreview() {
    GominittaTheme {
        SessionActiveContent(
            innerPadding = PaddingValues(0.dp),
            worryTitle = "UMC 프론트가 안 구해지면 어떡하지",
            worryMemo = "걱정걱정걱정",
            selectedTab = RecordTab.Text,
            onTabSelected = {},
            noteText = "",
            onNoteTextChange = {},
            isListening = false,
            onMicClick = {},
            isRecognizingImage = false,
            onCameraClick = {},
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
            worryTitle = "UMC 프론트가 안 구해지면 어떡하지",
            worryMemo = "걱정걱정걱정",
            selectedTab = RecordTab.Voice,
            onTabSelected = {},
            noteText = "",
            onNoteTextChange = {},
            isListening = false,
            onMicClick = {},
            isRecognizingImage = false,
            onCameraClick = {},
            onNavigateBack = {},
            onCompleteSession = {},
        )
    }
}

@Preview(name = "SessionActive - 카메라 탭", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun SessionActiveContentCameraPreview() {
    GominittaTheme {
        SessionActiveContent(
            innerPadding = PaddingValues(0.dp),
            worryTitle = "UMC 프론트가 안 구해지면 어떡하지",
            worryMemo = "걱정걱정걱정",
            selectedTab = RecordTab.Camera,
            onTabSelected = {},
            noteText = "",
            onNoteTextChange = {},
            isListening = false,
            onMicClick = {},
            isRecognizingImage = false,
            onCameraClick = {},
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
