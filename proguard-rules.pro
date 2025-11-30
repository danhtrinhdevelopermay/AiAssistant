# Add project specific ProGuard rules here.

# Keep Gemini AI classes
-keep class com.google.ai.client.generativeai.** { *; }
-keep class com.google.ai.client.generativeai.type.** { *; }

# Keep Hilt classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep data classes
-keep class com.xiaoai.assistant.data.** { *; }

# Voice Interaction
-keep class * extends android.service.voice.VoiceInteractionService
-keep class * extends android.service.voice.VoiceInteractionSessionService
-keep class * extends android.service.voice.VoiceInteractionSession
