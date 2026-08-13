package com.gominitta.android.presentation.session

import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.gominitta.android.ui.theme.Heading3_20m
import com.gominitta.android.ui.theme.Heading5_15m
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary300
import com.gominitta.android.ui.theme.Primary400
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb
import com.gominitta.android.ui.theme.White800
import java.io.File

/**
 * 마음 세션 진행 (C102 인트로 바텀시트 + C103 세션 기록 3종). 세션 상세 → 시작.
 * 첫 진입 시 "더 나은 기분으로 시작해볼까요?" 바텀시트가 한 번 뜨고, 아래엔 걱정 기록용
 * 텍스트/음성/사진 3탭이 있다.
 *
 * 텍스트는 "세션 완료하기" 시점에 한 번에 저장하고, 음성/사진은 녹음 정지·촬영 즉시
 * 서버로 업로드해 STT/OCR 결과를 기록으로 저장한다(성공 시 "저장됐어요" 뱃지가
 * 잠깐 떴다 사라짐). 걱정 내용은 [SessionActiveViewModel]이 실제 세션 상세 API로
 * 불러온다. 세션 로딩 실패는 화면 전체를 에러로 대체하고, 기록 저장 실패는 화면은
 * 그대로 둔 채 버튼 위에 인라인으로만 보여준다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionActiveScreen(
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToRecipeCenter: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionActiveViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // 새로 진입할 때만 띄운다 — 뒤로 돌아왔을 땐 유지되도록 rememberSaveable.
    var showIntroSheet by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(uiState.isDone) {
        if (uiState.isDone) {
            onNavigateNext()
            viewModel.onDoneHandled()
        }
    }
    LaunchedEffect(uiState.isExited) {
        if (uiState.isExited) {
            onNavigateBack()
            viewModel.onExitHandled()
        }
    }
    BackHandler { viewModel.saveNoteAndExit() }

    // 기록 확인 화면에서 고친 내용을 갖고 돌아왔을 때 텍스트 칸에 반영한다.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.syncEditedNote()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ) { innerPadding ->
            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Primary800)
                }
                uiState.loadErrorMessage != null -> Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = uiState.loadErrorMessage.orEmpty(), style = Body2_15r, color = Gray400, textAlign = TextAlign.Center)
                }
                else -> SessionActiveContent(
                    innerPadding = innerPadding,
                    worryTitle = uiState.worryTitle,
                    worryContent = uiState.worryContent,
                    themeCategory = uiState.themeCategory,
                    selectedTab = uiState.selectedTab,
                    onTabSelected = viewModel::selectTab,
                    noteText = uiState.noteText,
                    onNoteTextChange = viewModel::updateNoteText,
                    isSaving = uiState.isSaving,
                    capturedTab = uiState.capturedTab,
                    recordErrorMessage = uiState.recordErrorMessage,
                    onVoiceRecorded = viewModel::uploadVoiceRecord,
                    onHandwritingCaptured = viewModel::uploadHandwritingRecord,
                    onNavigateBack = viewModel::saveNoteAndExit,
                    onCompleteSession = viewModel::commitAndProceed,
                )
            }
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
            shape = RoundedCornerShape(0.dp),
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
    worryContent: String,
    themeCategory: String,
    selectedTab: RecordTab,
    onTabSelected: (RecordTab) -> Unit,
    noteText: String,
    onNoteTextChange: (String) -> Unit,
    isSaving: Boolean,
    capturedTab: RecordTab?,
    recordErrorMessage: String?,
    onVoiceRecorded: (File) -> Unit,
    onHandwritingCaptured: (File) -> Unit,
    onNavigateBack: () -> Unit,
    onCompleteSession: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)
            .imePadding()
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 28.dp),
    ) {
        Box(Modifier.fillMaxWidth()) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = "뒤로",
                tint = Primary800,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(32.dp)
                    .clickable(onClick = onNavigateBack),
            )
            Text(
                text = "마음 세션",
                style = Heading3_20m,
                color = Gray800,
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
            Text(text = "예약된 걱정", style = Heading5_15m, color = Gray800)
            Spacer(Modifier.height(8.dp))
            GominittaElevatedCard(modifier = Modifier.height(170.dp)) {
                Text(text = worryTitle, style = Body1_16m, color = Gray800)
                Spacer(Modifier.height(4.dp))
                Text(text = worryContent, style = Body2_15r, color = Gray800)
                Spacer(Modifier.height(4.dp))
                Text(text = themeCategory, style = Body3_14r, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                RecordTab.Voice -> VoiceRecordArea(
                    isUploading = isSaving,
                    onRecorded = onVoiceRecorded,
                )
                RecordTab.Handwriting -> HandwritingRecordArea(
                    isUploading = isSaving,
                    onCaptured = onHandwritingCaptured,
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        val capturedLabel = when (capturedTab) {
            RecordTab.Voice -> "녹음이 저장됐어요."
            RecordTab.Handwriting -> "사진이 저장됐어요."
            else -> null
        }
        AnimatedVisibility(visible = capturedLabel != null, enter = fadeIn(), exit = fadeOut()) {
            Text(
                text = capturedLabel.orEmpty(),
                style = Body3_14r,
                color = Primary800,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            )
        }
        if (recordErrorMessage != null) {
            Text(
                text = recordErrorMessage,
                style = Body3_14r,
                color = Gray400,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            )
        }

        GominittaButton(
            text = "세션 완료하기",
            onClick = onCompleteSession,
            // 업로드/저장 중엔 탭이 조용히 삼켜지므로 눌리지 않게 막는다.
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth(),
        )
    }
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
                textStyle = Body2_15r.copy(color = Gray800),
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

/** cacheDir 안에 촬영/녹음 파일을 임시로 담아둘 디렉터리. 업로드 후 바로 지운다. */
private fun sessionCaptureFile(context: Context, name: String): File {
    val dir = File(context.cacheDir, "session_captures").apply { mkdirs() }
    return File(dir, name)
}

@Composable
private fun VoiceRecordArea(
    isUploading: Boolean,
    onRecorded: (File) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var outputFile by remember { mutableStateOf<File?>(null) }

    fun stopAndDeliver() {
        val file = outputFile
        runCatching {
            recorder?.stop()
            recorder?.release()
        }
        recorder = null
        outputFile = null
        isRecording = false
        if (file != null) onRecorded(file)
    }

    fun startRecording() {
        val file = sessionCaptureFile(context, "voice_${System.currentTimeMillis()}.m4a")
        @Suppress("DEPRECATION")
        val newRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        recorder = newRecorder
        outputFile = file
        isRecording = true
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) startRecording()
    }

    DisposableEffect(Unit) {
        onDispose {
            runCatching {
                recorder?.stop()
                recorder?.release()
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "지금 드는 생각을 자유롭게 털어놔보세요. 중간중간 마이크를 눌러 멈춰도 돼요. " +
                "다 끝나면 세션 완료하기를 누르세요.",
            style = Body2_15r,
            color = Gray400,
        )
        Spacer(Modifier.height(48.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(140.dp),
            contentAlignment = Alignment.Center,
        ) {
            when {
                isUploading -> CircularProgressIndicator(color = Primary800)
                else -> Box(
                    modifier = Modifier
                        .size(126.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (isRecording) {
                                stopAndDeliver()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    if (isRecording) {
                        MicListeningPulse()
                    }
                    Icon(
                        painter = painterResource(R.drawable.ic_mic),
                        contentDescription = if (isRecording) "녹음 종료" else "녹음 시작",
                        tint = Primary400,
                        modifier = Modifier.size(100.dp),
                    )
                }
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
private fun HandwritingRecordArea(
    isUploading: Boolean,
    onCaptured: (File) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var pendingFile by remember { mutableStateOf<File?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        val file = pendingFile
        pendingFile = null
        if (success && file != null) onCaptured(file)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "노트나 일기장에 적어둔 내용이 있다면 카메라로 스캔해 보세요.",
            style = Body2_15r,
            color = Gray400,
        )
        Spacer(Modifier.height(48.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(140.dp),
            contentAlignment = Alignment.Center,
        ) {
            when {
                isUploading -> CircularProgressIndicator(color = Primary800)
                else -> Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = "카메라로 촬영",
                    tint = Primary400,
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            val file = sessionCaptureFile(context, "handwriting_${System.currentTimeMillis()}.jpg")
                            pendingFile = file
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            cameraLauncher.launch(uri)
                        },
                )
            }
        }
    }
}

@Composable
private fun SessionIntroSheetContent(onSkip: () -> Unit, onStartRecipe: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(R.drawable.session_right_leaf),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 18.96.dp, y = (-65.01).dp)
                .size(width = 117.dp, height = 105.dp),
        )

        Image(
            painter = painterResource(R.drawable.home_session_leaf),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = 76.79.dp)
                .size(width = 79.35.dp, height = 76.72.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(235.dp) // 283 - 드래그 핸들 영역(48dp)
                .padding(horizontal = 20.dp)
                .padding(bottom = 44.dp, top = 8.dp), // 드래그 핸들(48) + 8 = 맨 위에서 56dp
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "더 나은 기분으로 시작해볼까요?",
                style = Title1_20sb,
                color = Gray800,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "안정된 기분으로 고민을 마주하면\n더 차분하게 정리할 수 있어요.",
                style = Body1_16m,
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
}

// ---- Preview ---------------------------------------------------------------

private const val PREVIEW_WORRY_TITLE = "취업 걱정"
private const val PREVIEW_WORRY_CONTENT = "UMC 프론트가 안 구해지면 어떡하지"
private const val PREVIEW_THEME_CATEGORY = "진로"

@Preview(name = "SessionActive - 텍스트 탭", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun SessionActiveContentTextPreview() {
    GominittaTheme {
        SessionActiveContent(
            innerPadding = PaddingValues(0.dp),
            worryTitle = PREVIEW_WORRY_TITLE,
            worryContent = PREVIEW_WORRY_CONTENT,
            themeCategory = PREVIEW_THEME_CATEGORY,
            selectedTab = RecordTab.Text,
            onTabSelected = {},
            noteText = "",
            onNoteTextChange = {},
            isSaving = false,
            capturedTab = null,
            recordErrorMessage = null,
            onVoiceRecorded = {},
            onHandwritingCaptured = {},
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
            worryTitle = PREVIEW_WORRY_TITLE,
            worryContent = PREVIEW_WORRY_CONTENT,
            themeCategory = PREVIEW_THEME_CATEGORY,
            selectedTab = RecordTab.Voice,
            onTabSelected = {},
            noteText = "",
            onNoteTextChange = {},
            isSaving = false,
            capturedTab = null,
            recordErrorMessage = null,
            onVoiceRecorded = {},
            onHandwritingCaptured = {},
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
