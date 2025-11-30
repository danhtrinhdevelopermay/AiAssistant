package com.xiaoai.assistant.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.xiaoai.assistant.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "xiaoai_settings")

class PreferencesManager(private val context: Context) {
    
    companion object {
        private val API_KEY = stringPreferencesKey("gemini_api_key")
        private val VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val DEFAULT_ASSISTANT_SET = booleanPreferencesKey("default_assistant_set")
    }
    
    private val buildConfigApiKey: String = BuildConfig.GEMINI_API_KEY
    
    val apiKeyFlow: Flow<String> = context.dataStore.data.map { preferences ->
        val storedKey = preferences[API_KEY] ?: ""
        if (storedKey.isNotEmpty()) storedKey else buildConfigApiKey
    }
    
    val voiceEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[VOICE_ENABLED] ?: true
    }
    
    val onboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] ?: false
    }
    
    val defaultAssistantSetFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_ASSISTANT_SET] ?: false
    }
    
    fun getApiKey(): String = runBlocking {
        val storedKey = context.dataStore.data.first()[API_KEY] ?: ""
        if (storedKey.isNotEmpty()) storedKey else buildConfigApiKey
    }
    
    suspend fun setApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[API_KEY] = apiKey
        }
    }
    
    suspend fun setVoiceEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VOICE_ENABLED] = enabled
        }
    }
    
    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }
    
    suspend fun setDefaultAssistantSet(set: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_ASSISTANT_SET] = set
        }
    }
}
