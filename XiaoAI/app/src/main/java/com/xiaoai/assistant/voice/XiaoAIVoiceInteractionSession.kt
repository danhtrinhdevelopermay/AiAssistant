package com.xiaoai.assistant.voice

import android.app.assist.AssistContent
import android.app.assist.AssistStructure
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.util.Log
import com.xiaoai.assistant.ui.assistant.AssistantActivity

class XiaoAIVoiceInteractionSession(context: Context) : VoiceInteractionSession(context) {
    
    companion object {
        private const val TAG = "XiaoAISession"
    }
    
    override fun onShow(args: Bundle?, showFlags: Int) {
        super.onShow(args, showFlags)
        Log.d(TAG, "XiaoAI Session shown with flags: $showFlags")
        
        val intent = Intent(context, AssistantActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }
        context.startActivity(intent)
    }
    
    override fun onHandleAssist(
        data: Bundle?,
        structure: AssistStructure?,
        content: AssistContent?
    ) {
        super.onHandleAssist(data, structure, content)
        Log.d(TAG, "Handling assist request")
        
        structure?.let {
            val windowCount = it.windowNodeCount
            Log.d(TAG, "Received $windowCount window(s) from assist structure")
        }
        
        content?.let {
            Log.d(TAG, "Assist content web URI: ${it.webUri}")
        }
    }
    
    override fun onHandleAssistSecondary(
        data: Bundle?,
        structure: AssistStructure?,
        content: AssistContent?,
        index: Int,
        count: Int
    ) {
        super.onHandleAssistSecondary(data, structure, content, index, count)
        Log.d(TAG, "Handling secondary assist: $index of $count")
    }
    
    override fun onHandleScreenshot(screenshot: Bitmap?) {
        super.onHandleScreenshot(screenshot)
        screenshot?.let {
            Log.d(TAG, "Received screenshot: ${it.width}x${it.height}")
        }
    }
    
    override fun onHide() {
        super.onHide()
        Log.d(TAG, "XiaoAI Session hidden")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "XiaoAI Session destroyed")
    }
}
