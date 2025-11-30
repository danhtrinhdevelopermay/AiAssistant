package com.xiaoai.assistant.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

sealed class TextToSpeechState {
    object Idle : TextToSpeechState()
    object Speaking : TextToSpeechState()
    object Done : TextToSpeechState()
    data class Error(val message: String) : TextToSpeechState()
}

@Singleton
class TextToSpeechManager @Inject constructor(
    private val context: Context
) {
    
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    
    fun initialize(): Flow<Boolean> = callbackFlow {
        textToSpeech = TextToSpeech(context) { status ->
            isInitialized = status == TextToSpeech.SUCCESS
            if (isInitialized) {
                textToSpeech?.language = Locale("vi", "VN")
                textToSpeech?.setSpeechRate(1.0f)
                textToSpeech?.setPitch(1.0f)
            }
            trySend(isInitialized)
        }
        
        awaitClose {
            // Keep TTS alive
        }
    }
    
    fun speak(text: String): Flow<TextToSpeechState> = callbackFlow {
        if (!isInitialized || textToSpeech == null) {
            trySend(TextToSpeechState.Error("Text-to-Speech chưa được khởi tạo"))
            close()
            return@callbackFlow
        }
        
        val utteranceId = System.currentTimeMillis().toString()
        
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                trySend(TextToSpeechState.Speaking)
            }
            
            override fun onDone(utteranceId: String?) {
                trySend(TextToSpeechState.Done)
            }
            
            override fun onError(utteranceId: String?) {
                trySend(TextToSpeechState.Error("Lỗi phát âm"))
            }
            
            @Deprecated("Deprecated in API 21")
            override fun onError(utteranceId: String?, errorCode: Int) {
                trySend(TextToSpeechState.Error("Lỗi phát âm: $errorCode"))
            }
        })
        
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        
        awaitClose {
            // Keep speaking until done
        }
    }
    
    fun stop() {
        textToSpeech?.stop()
    }
    
    fun destroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }
    
    fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking ?: false
    }
}
