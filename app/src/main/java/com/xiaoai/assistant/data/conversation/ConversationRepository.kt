package com.xiaoai.assistant.data.conversation

import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class Message(
    val id: String = System.currentTimeMillis().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String? = null,
    val videoUri: String? = null,
    val isLoading: Boolean = false
)

@Singleton
class ConversationRepository @Inject constructor() {
    
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    
    fun addMessage(message: Message) {
        _messages.value = _messages.value + message
    }
    
    fun updateLastMessage(content: String) {
        val currentMessages = _messages.value.toMutableList()
        if (currentMessages.isNotEmpty()) {
            val lastMessage = currentMessages.last()
            currentMessages[currentMessages.size - 1] = lastMessage.copy(
                content = lastMessage.content + content,
                isLoading = false
            )
            _messages.value = currentMessages
        }
    }
    
    fun setLastMessageLoading(isLoading: Boolean) {
        val currentMessages = _messages.value.toMutableList()
        if (currentMessages.isNotEmpty()) {
            val lastMessage = currentMessages.last()
            currentMessages[currentMessages.size - 1] = lastMessage.copy(isLoading = isLoading)
            _messages.value = currentMessages
        }
    }
    
    fun replaceLastMessage(message: Message) {
        val currentMessages = _messages.value.toMutableList()
        if (currentMessages.isNotEmpty()) {
            currentMessages[currentMessages.size - 1] = message
            _messages.value = currentMessages
        }
    }
    
    fun clearMessages() {
        _messages.value = emptyList()
    }
    
    fun getConversationHistory(): List<Content> {
        return _messages.value.map { message ->
            content(role = if (message.isUser) "user" else "model") {
                text(message.content)
            }
        }
    }
    
    fun getLastUserMessage(): Message? {
        return _messages.value.lastOrNull { it.isUser }
    }
    
    fun getMessagesCount(): Int = _messages.value.size
}
