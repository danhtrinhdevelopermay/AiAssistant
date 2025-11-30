package com.xiaoai.assistant

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class XiaoAIApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
    }
}
