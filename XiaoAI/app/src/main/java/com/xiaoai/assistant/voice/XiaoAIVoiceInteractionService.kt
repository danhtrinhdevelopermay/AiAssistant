package com.xiaoai.assistant.voice

import android.service.voice.VoiceInteractionService
import android.util.Log

class XiaoAIVoiceInteractionService : VoiceInteractionService() {
    
    companion object {
        private const val TAG = "XiaoAIVoiceService"
    }
    
    override fun onReady() {
        super.onReady()
        Log.d(TAG, "XiaoAI Voice Interaction Service is ready")
    }
    
    override fun onShutdown() {
        super.onShutdown()
        Log.d(TAG, "XiaoAI Voice Interaction Service is shutting down")
    }
}
