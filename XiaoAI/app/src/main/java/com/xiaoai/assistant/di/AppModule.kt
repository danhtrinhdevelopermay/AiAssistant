package com.xiaoai.assistant.di

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.xiaoai.assistant.BuildConfig
import com.xiaoai.assistant.data.gemini.GeminiClient
import com.xiaoai.assistant.data.conversation.ConversationRepository
import com.xiaoai.assistant.speech.SpeechRecognizerWrapper
import com.xiaoai.assistant.speech.TextToSpeechManager
import com.xiaoai.assistant.util.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }
    
    @Provides
    @Singleton
    fun provideGeminiClient(
        preferencesManager: PreferencesManager
    ): GeminiClient {
        return GeminiClient(preferencesManager)
    }
    
    @Provides
    @Singleton
    fun provideConversationRepository(): ConversationRepository {
        return ConversationRepository()
    }
    
    @Provides
    @Singleton
    fun provideSpeechRecognizerWrapper(
        @ApplicationContext context: Context
    ): SpeechRecognizerWrapper {
        return SpeechRecognizerWrapper(context)
    }
    
    @Provides
    @Singleton
    fun provideTextToSpeechManager(
        @ApplicationContext context: Context
    ): TextToSpeechManager {
        return TextToSpeechManager(context)
    }
}
