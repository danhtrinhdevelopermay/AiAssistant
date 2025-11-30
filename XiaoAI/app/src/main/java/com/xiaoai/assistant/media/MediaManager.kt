package com.xiaoai.assistant.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaManager @Inject constructor(
    private val context: Context
) {
    
    suspend fun loadBitmapFromUri(uri: Uri): Result<Bitmap> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (bitmap != null) {
                val resizedBitmap = resizeBitmapIfNeeded(bitmap)
                Result.success(resizedBitmap)
            } else {
                Result.failure(Exception("Không thể đọc hình ảnh"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun resizeBitmapIfNeeded(bitmap: Bitmap, maxSize: Int = 1024): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }
        
        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        
        if (width > height) {
            newWidth = maxSize
            newHeight = (maxSize / ratio).toInt()
        } else {
            newHeight = maxSize
            newWidth = (maxSize * ratio).toInt()
        }
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    suspend fun extractVideoFrames(uri: Uri, frameCount: Int = 5): Result<List<Bitmap>> = withContext(Dispatchers.IO) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val duration = durationStr?.toLongOrNull() ?: 0L
            
            if (duration == 0L) {
                retriever.release()
                return@withContext Result.failure(Exception("Không thể đọc video"))
            }
            
            val frames = mutableListOf<Bitmap>()
            val interval = duration / (frameCount + 1)
            
            for (i in 1..frameCount) {
                val timeUs = interval * i * 1000
                val frame = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                frame?.let {
                    frames.add(resizeBitmapIfNeeded(it, 512))
                }
            }
            
            retriever.release()
            
            if (frames.isEmpty()) {
                Result.failure(Exception("Không thể trích xuất khung hình từ video"))
            } else {
                Result.success(frames)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun bitmapToByteArray(bitmap: Bitmap, quality: Int = 80): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }
}
