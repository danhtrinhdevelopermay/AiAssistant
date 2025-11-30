package com.xiaoai.assistant.voice

import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService
import android.util.Log

class XiaoAIVoiceInteractionSessionService : VoiceInteractionSessionService() {
    
    companion object {
        private const val TAG = "XiaoAISessionService"
    }
    
    override fun onNewSession(args: Bundle?): VoiceInteractionSession {
        Log.d(TAG, "Creating new XiaoAI voice interaction session")
        return XiaoAIVoiceInteractionSession(this)
    }
}
