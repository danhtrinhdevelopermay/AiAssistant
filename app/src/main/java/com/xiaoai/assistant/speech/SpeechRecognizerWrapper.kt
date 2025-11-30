package com.xiaoai.assistant.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class SpeechRecognitionState {
    object Idle : SpeechRecognitionState()
    object Listening : SpeechRecognitionState()
    data class Result(val text: String) : SpeechRecognitionState()
    data class PartialResult(val text: String) : SpeechRecognitionState()
    data class Error(val message: String) : SpeechRecognitionState()
}

@Singleton
class SpeechRecognizerWrapper @Inject constructor(
    private val context: Context
) {
    
    private var speechRecognizer: SpeechRecognizer? = null
    
    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }
    
    fun startListening(): Flow<SpeechRecognitionState> = callbackFlow {
        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                trySend(SpeechRecognitionState.Listening)
            }
            
            override fun onBeginningOfSpeech() {}
            
            override fun onRmsChanged(rmsdB: Float) {}
            
            override fun onBufferReceived(buffer: ByteArray?) {}
            
            override fun onEndOfSpeech() {}
            
            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Lỗi ghi âm"
                    SpeechRecognizer.ERROR_CLIENT -> "Lỗi client"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Không có quyền ghi âm"
                    SpeechRecognizer.ERROR_NETWORK -> "Lỗi mạng"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Hết thời gian kết nối"
                    SpeechRecognizer.ERROR_NO_MATCH -> "Không nhận diện được giọng nói"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Bộ nhận diện đang bận"
                    SpeechRecognizer.ERROR_SERVER -> "Lỗi server"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Không nghe thấy giọng nói"
                    else -> "Lỗi không xác định"
                }
                trySend(SpeechRecognitionState.Error(errorMessage))
            }
            
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotEmpty()) {
                    trySend(SpeechRecognitionState.Result(text))
                }
            }
            
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotEmpty()) {
                    trySend(SpeechRecognitionState.PartialResult(text))
                }
            }
            
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        
        speechRecognizer?.startListening(intent)
        
        awaitClose {
            stopListening()
        }
    }
    
    fun stopListening() {
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
    }
    
    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
