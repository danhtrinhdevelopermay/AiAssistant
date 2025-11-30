package com.xiaoai.assistant.data.gemini

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import com.xiaoai.assistant.util.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiClient @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    
    private var generativeModel: GenerativeModel? = null
    private var visionModel: GenerativeModel? = null
    
    private val systemPrompt = """
        Bạn là XiaoAI, một trợ lý ảo thông minh và thân thiện. 
        Bạn được tạo ra để hỗ trợ người dùng trong mọi công việc hàng ngày.
        
        Quy tắc:
        - Luôn trả lời bằng tiếng Việt trừ khi người dùng yêu cầu ngôn ngữ khác
        - Trả lời ngắn gọn, súc tích nhưng đầy đủ thông tin
        - Thân thiện và lịch sự trong mọi tình huống
        - Có thể phân tích hình ảnh và video khi được yêu cầu
        - Nếu không biết câu trả lời, hãy thừa nhận thay vì bịa đặt
    """.trimIndent()
    
    private fun getTextModel(): GenerativeModel {
        val apiKey = preferencesManager.getApiKey()
        if (apiKey.isEmpty()) {
            throw IllegalStateException("API key chưa được cấu hình")
        }
        
        if (generativeModel == null) {
            generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )
        }
        return generativeModel!!
    }
    
    private fun getVisionModel(): GenerativeModel {
        val apiKey = preferencesManager.getApiKey()
        if (apiKey.isEmpty()) {
            throw IllegalStateException("API key chưa được cấu hình")
        }
        
        if (visionModel == null) {
            visionModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )
        }
        return visionModel!!
    }
    
    suspend fun generateTextResponse(
        prompt: String,
        conversationHistory: List<Content> = emptyList()
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val model = getTextModel()
            val chat = model.startChat(conversationHistory)
            
            val fullPrompt = if (conversationHistory.isEmpty()) {
                "$systemPrompt\n\nNgười dùng: $prompt"
            } else {
                prompt
            }
            
            val response = chat.sendMessage(fullPrompt)
            Result.success(response.text ?: "Không có phản hồi")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun generateTextResponseStream(
        prompt: String,
        conversationHistory: List<Content> = emptyList()
    ): Flow<String> = flow {
        try {
            val model = getTextModel()
            val chat = model.startChat(conversationHistory)
            
            val fullPrompt = if (conversationHistory.isEmpty()) {
                "$systemPrompt\n\nNgười dùng: $prompt"
            } else {
                prompt
            }
            
            chat.sendMessageStream(fullPrompt).collect { chunk ->
                chunk.text?.let { emit(it) }
            }
        } catch (e: Exception) {
            emit("Lỗi: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)
    
    suspend fun analyzeImage(
        image: Bitmap,
        prompt: String = "Hãy mô tả chi tiết hình ảnh này"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val model = getVisionModel()
            
            val inputContent = content {
                image(image)
                text("$systemPrompt\n\n$prompt")
            }
            
            val response = model.generateContent(inputContent)
            Result.success(response.text ?: "Không thể phân tích hình ảnh")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun analyzeImageWithQuestion(
        image: Bitmap,
        question: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val model = getVisionModel()
            
            val inputContent = content {
                image(image)
                text("$systemPrompt\n\nCâu hỏi về hình ảnh: $question")
            }
            
            val response = model.generateContent(inputContent)
            Result.success(response.text ?: "Không thể trả lời câu hỏi về hình ảnh")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun analyzeVideo(
        videoFrames: List<Bitmap>,
        prompt: String = "Hãy phân tích và mô tả nội dung video này"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val model = getVisionModel()
            
            val inputContent = content {
                videoFrames.forEachIndexed { index, frame ->
                    image(frame)
                    if (index < videoFrames.size - 1) {
                        text("[Khung hình ${index + 1}]")
                    }
                }
                text("$systemPrompt\n\nĐây là các khung hình từ một video. $prompt")
            }
            
            val response = model.generateContent(inputContent)
            Result.success(response.text ?: "Không thể phân tích video")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun clearModels() {
        generativeModel = null
        visionModel = null
    }
}
