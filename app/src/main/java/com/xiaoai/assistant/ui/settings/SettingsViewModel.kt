package com.xiaoai.assistant.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xiaoai.assistant.data.gemini.GeminiClient
import com.xiaoai.assistant.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val apiKey: String = "",
    val voiceEnabled: Boolean = true,
    val isLoading: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val geminiClient: GeminiClient
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            val apiKey = preferencesManager.apiKeyFlow.first()
            val voiceEnabled = preferencesManager.voiceEnabledFlow.first()
            
            _uiState.value = SettingsUiState(
                apiKey = apiKey,
                voiceEnabled = voiceEnabled,
                isLoading = false
            )
        }
        
        viewModelScope.launch {
            preferencesManager.apiKeyFlow.collect { apiKey ->
                _uiState.value = _uiState.value.copy(apiKey = apiKey)
            }
        }
        
        viewModelScope.launch {
            preferencesManager.voiceEnabledFlow.collect { enabled ->
                _uiState.value = _uiState.value.copy(voiceEnabled = enabled)
            }
        }
    }
    
    fun setApiKey(apiKey: String) {
        viewModelScope.launch {
            preferencesManager.setApiKey(apiKey)
            geminiClient.clearModels()
        }
    }
    
    fun setVoiceEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setVoiceEnabled(enabled)
        }
    }
}
