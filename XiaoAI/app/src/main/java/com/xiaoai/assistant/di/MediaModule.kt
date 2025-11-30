package com.xiaoai.assistant.di

import android.content.Context
import com.xiaoai.assistant.media.MediaManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {
    
    @Provides
    @Singleton
    fun provideMediaManager(
        @ApplicationContext context: Context
    ): MediaManager {
        return MediaManager(context)
    }
}
