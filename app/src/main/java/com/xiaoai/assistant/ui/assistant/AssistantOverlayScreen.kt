package com.xiaoai.assistant.ui.assistant

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.xiaoai.assistant.R
import com.xiaoai.assistant.data.conversation.Message

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AssistantOverlayScreen(
    onDismiss: () -> Unit,
    viewModel: AssistantViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    
    val audioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.selectImage(it) }
    }
    
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.selectVideo(it) }
    }
    
    var showMediaOptions by remember { mutableStateOf(false) }
    
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    LaunchedEffect(Unit) {
        if (audioPermissionState.status.isGranted) {
            viewModel.startListening()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { onDismiss() }
            }
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .pointerInput(Unit) {
                    detectTapGestures { }
                },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Assistant,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "XiaoAI",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng")
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (messages.isEmpty() && !uiState.isListening) {
                        item {
                            OverlayWelcomeMessage()
                        }
                    }
                    
                    items(messages) { message ->
                        OverlayMessageBubble(message = message)
                    }
                }
                
                AnimatedVisibility(visible = uiState.isListening) {
                    ListeningIndicator(partialResult = uiState.partialSpeechResult)
                }
                
                AnimatedVisibility(
                    visible = uiState.selectedImageUri != null || uiState.selectedVideoUri != null
                ) {
                    MediaPreview(
                        imageUri = uiState.selectedImageUri,
                        videoUri = uiState.selectedVideoUri,
                        onClear = { viewModel.clearMedia() }
                    )
                }
                
                OverlayInputSection(
                    currentInput = uiState.currentInput,
                    isListening = uiState.isListening,
                    isProcessing = uiState.isProcessing,
                    isSpeaking = uiState.isSpeaking,
                    onInputChange = { viewModel.updateInput(it) },
                    onSend = { viewModel.sendMessage() },
                    onMicClick = {
                        if (audioPermissionState.status.isGranted) {
                            if (uiState.isListening) {
                                viewModel.stopListening()
                            } else {
                                viewModel.startListening()
                            }
                        } else {
                            audioPermissionState.launchPermissionRequest()
                        }
                    },
                    onStopSpeaking = { viewModel.stopSpeaking() },
                    onAttachClick = { showMediaOptions = !showMediaOptions }
                )
                
                AnimatedVisibility(visible = showMediaOptions) {
                    MediaOptionsBar(
                        onImageClick = {
                            imagePickerLauncher.launch("image/*")
                            showMediaOptions = false
                        },
                        onVideoClick = {
                            videoPickerLauncher.launch("video/*")
                            showMediaOptions = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OverlayWelcomeMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Assistant,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Xin chào! Tôi có thể giúp gì cho bạn?",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OverlayMessageBubble(message: Message) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val backgroundColor = if (message.isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        message.imageUri?.let { uri ->
            AsyncImage(
                model = Uri.parse(uri),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(backgroundColor, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            if (message.isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đang xử lý...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ListeningIndicator(partialResult: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.assistant_listening),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (partialResult.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = partialResult,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun OverlayInputSection(
    currentInput: String,
    isListening: Boolean,
    isProcessing: Boolean,
    isSpeaking: Boolean,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit,
    onStopSpeaking: () -> Unit,
    onAttachClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onAttachClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm"
                )
            }
            
            OutlinedTextField(
                value = currentInput,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        if (isListening) "Đang nghe..."
                        else "Nhập tin nhắn...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                enabled = !isListening,
                singleLine = false,
                maxLines = 3,
                shape = RoundedCornerShape(20.dp),
                textStyle = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            when {
                isSpeaking -> {
                    FloatingActionButton(
                        onClick = onStopSpeaking,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeOff,
                            contentDescription = "Dừng nói"
                        )
                    }
                }
                currentInput.isNotEmpty() -> {
                    FloatingActionButton(
                        onClick = onSend,
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Gửi",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                else -> {
                    FloatingActionButton(
                        onClick = onMicClick,
                        containerColor = if (isListening) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isListening) "Dừng" else "Ghi âm"
                        )
                    }
                }
            }
        }
    }
}
