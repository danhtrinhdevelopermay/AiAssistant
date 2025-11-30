package com.xiaoai.assistant.ui.onboarding

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xiaoai.assistant.R
import kotlinx.coroutines.launch

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val action: OnboardingAction? = null
)

sealed class OnboardingAction {
    object OpenAssistantSettings : OnboardingAction()
    object OpenApiKeySettings : OnboardingAction()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Default.Assistant,
            title = "Chào mừng đến với XiaoAI",
            description = "Trợ lý ảo thông minh sử dụng Gemini AI, hỗ trợ bạn mọi lúc mọi nơi với khả năng phân tích hình ảnh và video."
        ),
        OnboardingPage(
            icon = Icons.Default.Mic,
            title = "Điều khiển bằng giọng nói",
            description = "XiaoAI có thể nghe và phản hồi bằng giọng nói. Bạn có thể đặt câu hỏi hoặc yêu cầu phân tích hình ảnh/video."
        ),
        OnboardingPage(
            icon = Icons.Default.SettingsApplications,
            title = "Đặt làm trợ lý mặc định",
            description = "Để sử dụng XiaoAI thay thế Google Assistant, hãy đặt XiaoAI làm trợ lý mặc định của thiết bị.",
            action = OnboardingAction.OpenAssistantSettings
        ),
        OnboardingPage(
            icon = Icons.Default.Key,
            title = "Cấu hình API Key",
            description = "Để XiaoAI hoạt động, bạn cần nhập API Key từ Google AI Studio. Bạn có thể làm điều này trong phần cài đặt.",
            action = OnboardingAction.OpenApiKeySettings
        )
    )
    
    val pagerState = rememberPagerState(pageCount = { pages.size })
    
    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onComplete()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            if (pagerState.currentPage < pages.size - 1) {
                TextButton(onClick = { viewModel.completeOnboarding() }) {
                    Text(stringResource(R.string.onboarding_skip))
                }
            }
        }
        
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            OnboardingPageContent(
                page = pages[page],
                onAction = { action ->
                    when (action) {
                        OnboardingAction.OpenAssistantSettings -> {
                            try {
                                val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                try {
                                    val intent = Intent(Settings.ACTION_SETTINGS)
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                }
                            }
                        }
                        OnboardingAction.OpenApiKeySettings -> {
                            onNavigateToSettings()
                        }
                    }
                }
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            }
                        )
                )
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage > 0) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Trước")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
            
            if (pagerState.currentPage == pages.size - 1) {
                Button(
                    onClick = { viewModel.completeOnboarding() }
                ) {
                    Text(stringResource(R.string.onboarding_done))
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                ) {
                    Text(stringResource(R.string.onboarding_next))
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    onAction: (OnboardingAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        page.action?.let { action ->
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedButton(
                onClick = { onAction(action) },
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    imageVector = when (action) {
                        OnboardingAction.OpenAssistantSettings -> Icons.Default.Settings
                        OnboardingAction.OpenApiKeySettings -> Icons.Default.Key
                    },
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    when (action) {
                        OnboardingAction.OpenAssistantSettings -> stringResource(R.string.onboarding_open_settings)
                        OnboardingAction.OpenApiKeySettings -> "Cấu hình API Key"
                    }
                )
            }
        }
    }
}
