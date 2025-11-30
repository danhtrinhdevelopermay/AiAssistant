package com.xiaoai.assistant.ui.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.xiaoai.assistant.ui.theme.XiaoAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssistantActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            XiaoAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.5f)
                ) {
                    AssistantOverlayScreen(
                        onDismiss = { finish() }
                    )
                }
            }
        }
    }
}
