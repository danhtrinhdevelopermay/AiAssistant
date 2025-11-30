# XiaoAI - Android Virtual Assistant Project

## Project Overview
XiaoAI is an Android Kotlin application that serves as an intelligent virtual assistant, designed to replace Google Assistant. It uses Google Gemini AI for multimodal processing including text, image, and video analysis.

## Project Type
This is a **native Android application** that cannot be run directly in the Replit environment. It requires:
- Android Studio for development
- Android SDK 34
- JDK 17
- An Android device or emulator for testing

## Project Structure
```
XiaoAI/
├── app/                           # Main Android app module
│   ├── src/main/
│   │   ├── java/com/xiaoai/assistant/
│   │   │   ├── di/               # Hilt Dependency Injection
│   │   │   ├── voice/            # VoiceInteractionService for replacing Google Assistant
│   │   │   ├── ui/               # Jetpack Compose UI
│   │   │   │   ├── assistant/    # Main assistant screens
│   │   │   │   ├── onboarding/   # First-time setup flow
│   │   │   │   └── settings/     # App settings
│   │   │   ├── data/             # Data layer
│   │   │   │   ├── gemini/       # Gemini AI client
│   │   │   │   └── conversation/ # Chat history
│   │   │   ├── speech/           # Speech-to-Text & Text-to-Speech
│   │   │   └── media/            # Image/video processing
│   │   └── res/                  # Android resources
│   └── build.gradle.kts          # App build configuration
├── .github/workflows/            # GitHub Actions for APK builds
├── build.gradle.kts              # Root build configuration
└── settings.gradle.kts           # Gradle settings
```

## Key Features
1. **Voice Interaction Service**: Can be set as default assistant to replace Google Assistant
2. **Gemini AI Integration**: Multimodal AI for text, image, and video analysis
3. **Speech Recognition**: Vietnamese language support
4. **Text-to-Speech**: Voice responses
5. **Material Design 3**: Modern Android UI

## Building the APK

### Option 1: GitHub Actions (Recommended)
1. Push this code to a GitHub repository
2. Configure the following secrets in GitHub:
   - `GEMINI_API_KEY`: Your Google AI Studio API key
   - `KEYSTORE_BASE64` (optional): Base64-encoded keystore for signed releases
   - `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD` (optional)
3. The workflow will automatically build APK on push

### Option 2: Local Build with Android Studio
1. Clone the repository
2. Open in Android Studio
3. Add `GEMINI_API_KEY=your_key` to `local.properties`
4. Build and run on device/emulator

## Technology Stack
- Kotlin
- Jetpack Compose
- Hilt (Dependency Injection)
- Google Gemini AI SDK
- Android VoiceInteractionService API
- Coroutines & Flow
- DataStore Preferences

## Recent Changes
- Initial project creation with full XiaoAI implementation
- VoiceInteractionService setup for default assistant capability
- Gemini AI integration for text, image, and video analysis
- Complete UI with onboarding, assistant, and settings screens
- GitHub Actions workflow for automated APK builds

## Notes
- This is a mobile app project - it cannot run as a web service
- APK must be built externally (GitHub Actions or Android Studio)
- Requires Android 8.0 (API 26) or higher
