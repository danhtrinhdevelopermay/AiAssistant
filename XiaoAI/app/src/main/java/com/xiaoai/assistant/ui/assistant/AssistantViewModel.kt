package com.xiaoai.assistant.ui.assistant

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xiaoai.assistant.data.conversation.ConversationRepository
import com.xiaoai.assistant.data.conversation.Message
import com.xiaoai.assistant.data.gemini.GeminiClient
import com.xiaoai.assistant.media.MediaManager
import com.xiaoai.assistant.speech.SpeechRecognitionState
import com.xiaoai.assistant.speech.SpeechRecognizerWrapper
import com.xiaoai.assistant.speech.TextToSpeechManager
import com.xiaoai.assistant.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssistantUiState(
    val isListening: Boolean = false,
    val isSpeaking: Boolean = false,
    val isProcessing: Boolean = false,
    val currentInput: String = "",
    val partialSpeechResult: String = "",
    val error: String? = null,
    val selectedImageUri: Uri? = null,
    val selectedVideoUri: Uri? = null,
    val voiceEnabled: Boolean = true,
    val apiKeyConfigured: Boolean = false
)

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val geminiClient: GeminiClient,
    private val conversationRepository: ConversationRepository,
    private val speechRecognizer: SpeechRecognizerWrapper,
    private val textToSpeechManager: TextToSpeechManager,
    private val mediaManager: MediaManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()
    
    val messages = conversationRepository.messages
    
    init {
        viewModelScope.launch {
            preferencesManager.voiceEnabledFlow.collect { enabled ->
                _uiState.value = _uiState.value.copy(voiceEnabled = enabled)
            }
        }
        
        viewModelScope.launch {
            preferencesManager.apiKeyFlow.collect { apiKey ->
                _uiState.value = _uiState.value.copy(apiKeyConfigured = apiKey.isNotEmpty())
            }
        }
        
        viewModelScope.launch {
            textToSpeechManager.initialize().collect { }
        }
    }
    
    fun updateInput(input: String) {
        _uiState.value = _uiState.value.copy(currentInput = input)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun startListening() {
        if (!speechRecognizer.isAvailable()) {
            _uiState.value = _uiState.value.copy(error = "Speech recognition không khả dụng")
            return
        }
        
        _uiState.value = _uiState.value.copy(isListening = true, partialSpeechResult = "")
        
        viewModelScope.launch {
            speechRecognizer.startListening().collect { state ->
                when (state) {
                    is SpeechRecognitionState.Listening -> {
                        _uiState.value = _uiState.value.copy(isListening = true)
                    }
                    is SpeechRecognitionState.PartialResult -> {
                        _uiState.value = _uiState.value.copy(partialSpeechResult = state.text)
                    }
                    is SpeechRecognitionState.Result -> {
                        _uiState.value = _uiState.value.copy(
                            isListening = false,
                            currentInput = state.text,
                            partialSpeechResult = ""
                        )
                        sendMessage(state.text)
                    }
                    is SpeechRecognitionState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isListening = false,
                            error = state.message,
                            partialSpeechResult = ""
                        )
                    }
                    is SpeechRecognitionState.Idle -> {
                        _uiState.value = _uiState.value.copy(isListening = false)
                    }
                }
            }
        }
    }
    
    fun stopListening() {
        speechRecognizer.stopListening()
        _uiState.value = _uiState.value.copy(isListening = false, partialSpeechResult = "")
    }
    
    fun sendMessage(text: String? = null) {
        val messageText = text ?: _uiState.value.currentInput
        if (messageText.isBlank()) return
        
        val imageUri = _uiState.value.selectedImageUri
        val videoUri = _uiState.value.selectedVideoUri
        
        val userMessage = Message(
            content = messageText,
            isUser = true,
            imageUri = imageUri?.toString(),
            videoUri = videoUri?.toString()
        )
        conversationRepository.addMessage(userMessage)
        
        val aiMessage = Message(
            content = "",
            isUser = false,
            isLoading = true
        )
        conversationRepository.addMessage(aiMessage)
        
        _uiState.value = _uiState.value.copy(
            currentInput = "",
            isProcessing = true,
            selectedImageUri = null,
            selectedVideoUri = null
        )
        
        viewModelScope.launch {
            try {
                val response = when {
                    imageUri != null -> processImageMessage(imageUri, messageText)
                    videoUri != null -> processVideoMessage(videoUri, messageText)
                    else -> processTextMessage(messageText)
                }
                
                response.fold(
                    onSuccess = { responseText ->
                        conversationRepository.replaceLastMessage(
                            Message(content = responseText, isUser = false)
                        )
                        
                        if (_uiState.value.voiceEnabled) {
                            speakResponse(responseText)
                        }
                    },
                    onFailure = { error ->
                        conversationRepository.replaceLastMessage(
                            Message(content = "Lỗi: ${error.message}", isUser = false)
                        )
                    }
                )
            } finally {
                _uiState.value = _uiState.value.copy(isProcessing = false)
            }
        }
    }
    
    private suspend fun processTextMessage(text: String): Result<String> {
        return geminiClient.generateTextResponse(
            prompt = text,
            conversationHistory = conversationRepository.getConversationHistory().dropLast(2)
        )
    }
    
    private suspend fun processImageMessage(uri: Uri, text: String): Result<String> {
        return mediaManager.loadBitmapFromUri(uri).fold(
            onSuccess = { bitmap ->
                geminiClient.analyzeImageWithQuestion(bitmap, text)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
    
    private suspend fun processVideoMessage(uri: Uri, text: String): Result<String> {
        return mediaManager.extractVideoFrames(uri).fold(
            onSuccess = { frames ->
                geminiClient.analyzeVideo(frames, text)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
    
    private fun speakResponse(text: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSpeaking = true)
            textToSpeechManager.speak(text).collect { state ->
                when (state) {
                    is com.xiaoai.assistant.speech.TextToSpeechState.Done,
                    is com.xiaoai.assistant.speech.TextToSpeechState.Error -> {
                        _uiState.value = _uiState.value.copy(isSpeaking = false)
                    }
                    else -> {}
                }
            }
        }
    }
    
    fun stopSpeaking() {
        textToSpeechManager.stop()
        _uiState.value = _uiState.value.copy(isSpeaking = false)
    }
    
    fun selectImage(uri: Uri) {
        _uiState.value = _uiState.value.copy(selectedImageUri = uri, selectedVideoUri = null)
    }
    
    fun selectVideo(uri: Uri) {
        _uiState.value = _uiState.value.copy(selectedVideoUri = uri, selectedImageUri = null)
    }
    
    fun clearMedia() {
        _uiState.value = _uiState.value.copy(selectedImageUri = null, selectedVideoUri = null)
    }
    
    fun clearConversation() {
        conversationRepository.clearMessages()
    }
    
    override fun onCleared() {
        super.onCleared()
        speechRecognizer.destroy()
        textToSpeechManager.destroy()
    }
}
