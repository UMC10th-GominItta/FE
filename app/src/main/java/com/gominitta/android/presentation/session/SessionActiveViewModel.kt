package com.gominitta.android.presentation.session

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.gominitta.android.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@HiltViewModel
class SessionActiveViewModel @Inject constructor(
    private val repository: SessionRepository,
) : ViewModel() {

    var worryTitle by mutableStateOf("")
        private set
    var worryMemo by mutableStateOf("")
        private set

    /** 텍스트 입력 탭이든 음성 인식 탭이든, 결국 이 값 하나로 모여서 다음 화면(SessionDetail)에 넘어간다. */
    var noteText by mutableStateOf("")

    var isListening by mutableStateOf(false)
        private set

    var isRecognizingImage by mutableStateOf(false)
        private set

    private var loadedSessionId: Long? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private val koreanTextRecognizer by lazy {
        TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
    }

    /** true인 동안은 onResults/onError에서 계속 재시작해 "다시 누를 때까지" 듣는 것처럼 동작시킨다. */
    private var keepListening = false

    fun load(sessionId: Long) {
        if (loadedSessionId == sessionId) return
        loadedSessionId = sessionId
        viewModelScope.launch {
            try {
                val detail = repository.getSessionDetail(sessionId)
                worryTitle = detail.worryContent
                worryMemo = detail.worryMemo
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                loadedSessionId = null
                e.printStackTrace()
            }
        }
    }

    /** 마이크 버튼 토글 — 듣고 있지 않으면 시작, 듣고 있으면 종료. RECORD_AUDIO 권한은 호출부에서 이미 확인된 상태여야 한다. */
    fun toggleVoiceRecognition(context: Context) {
        if (isListening) stopVoiceRecognition() else startVoiceRecognition(context)
    }

    private fun startVoiceRecognition(context: Context) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) return
        keepListening = true
        isListening = true
        val recognizer = speechRecognizer ?: SpeechRecognizer.createSpeechRecognizer(context).also {
            it.setRecognitionListener(recognitionListener)
            speechRecognizer = it
        }
        recognizer.startListening(recognitionIntent())
    }

    private fun stopVoiceRecognition() {
        keepListening = false
        isListening = false
        speechRecognizer?.stopListening()
    }

    private fun recognitionIntent(): Intent =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

    private val recognitionListener = object : RecognitionListener {
        override fun onResults(results: Bundle?) {
            val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
            if (!text.isNullOrBlank()) {
                noteText = if (noteText.isBlank()) text else "$noteText $text"
            }
            if (keepListening) speechRecognizer?.startListening(recognitionIntent())
        }

        override fun onError(error: Int) {
            val recoverable = error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT
            if (keepListening && recoverable) {
                speechRecognizer?.startListening(recognitionIntent())
            } else {
                keepListening = false
                isListening = false
            }
        }

        override fun onReadyForSpeech(params: Bundle?) = Unit
        override fun onBeginningOfSpeech() = Unit
        override fun onRmsChanged(rmsdB: Float) = Unit
        override fun onBufferReceived(buffer: ByteArray?) = Unit
        override fun onEndOfSpeech() = Unit
        override fun onPartialResults(partialResults: Bundle?) = Unit
        override fun onEvent(eventType: Int, params: Bundle?) = Unit
    }

    /** 카메라로 찍은 [bitmap]에서 텍스트를 인식해 noteText에 누적한다. */
    fun recognizeImage(bitmap: Bitmap) {
        isRecognizingImage = true
        val image = InputImage.fromBitmap(bitmap, 0)
        koreanTextRecognizer.process(image)
            .addOnSuccessListener { result ->
                val text = result.text
                if (text.isNotBlank()) {
                    noteText = if (noteText.isBlank()) text else "$noteText $text"
                }
                isRecognizingImage = false
            }
            .addOnFailureListener {
                isRecognizingImage = false
            }
    }

    override fun onCleared() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        koreanTextRecognizer.close()
        super.onCleared()
    }
}
