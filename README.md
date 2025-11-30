# XiaoAI - Trợ Lý Ảo Thông Minh

XiaoAI là ứng dụng trợ lý ảo thông minh cho Android, được xây dựng bằng Kotlin và sử dụng Google Gemini AI. Ứng dụng có thể thay thế Google Assistant và hỗ trợ phân tích hình ảnh, video.

## Tính năng

- **Trợ lý mặc định**: Có thể đặt làm trợ lý mặc định thay thế Google Assistant
- **Nhận diện giọng nói**: Hỗ trợ điều khiển bằng giọng nói tiếng Việt
- **Phản hồi bằng giọng nói**: Text-to-Speech cho phản hồi tự nhiên
- **Phân tích hình ảnh**: Mô tả và trả lời câu hỏi về hình ảnh
- **Phân tích video**: Trích xuất và phân tích nội dung video
- **Giao diện Material Design 3**: UI hiện đại và thân thiện
- **Overlay anywhere**: Xuất hiện ở bất cứ đâu khi được gọi

## Yêu cầu

- Android 8.0 (API 26) trở lên
- Kết nối internet
- Microphone (cho nhận diện giọng nói)
- Camera (tùy chọn, cho chụp ảnh/quay video)

## Cài đặt

### Cách 1: Tải APK từ GitHub Releases
1. Vào phần [Releases](../../releases) của repository
2. Tải file APK mới nhất
3. Cài đặt trên thiết bị Android

### Cách 2: Build từ source code

#### Yêu cầu
- Android Studio Hedgehog (2023.1.1) trở lên
- JDK 17
- Android SDK 34

#### Các bước
1. Clone repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/XiaoAI.git
   cd XiaoAI
   ```

2. Mở project bằng Android Studio

3. Tạo file `local.properties` và thêm API key:
   ```properties
   GEMINI_API_KEY=YOUR_API_KEY_HERE
   ```

4. Build và chạy ứng dụng

## Cấu hình GitHub Actions

Để tự động build APK khi push code, cấu hình các secrets sau trong GitHub repository:

### Bắt buộc
- `GEMINI_API_KEY`: API key từ Google AI Studio

### Tùy chọn (cho release builds)
- `KEYSTORE_BASE64`: Keystore file được encode base64
- `KEYSTORE_PASSWORD`: Mật khẩu keystore
- `KEY_ALIAS`: Alias của key
- `KEY_PASSWORD`: Mật khẩu của key

### Cách tạo keystore:
```bash
keytool -genkey -v -keystore release.keystore -alias xiaoai -keyalg RSA -keysize 2048 -validity 10000
base64 release.keystore > keystore_base64.txt
```

Copy nội dung `keystore_base64.txt` vào secret `KEYSTORE_BASE64`.

## Cách sử dụng

### Đặt làm trợ lý mặc định
1. Mở ứng dụng XiaoAI
2. Làm theo hướng dẫn onboarding
3. Vào **Cài đặt > Ứng dụng > Ứng dụng mặc định > Trợ lý kỹ thuật số**
4. Chọn XiaoAI

### Cấu hình API Key
1. Truy cập [Google AI Studio](https://aistudio.google.com/)
2. Tạo API key mới
3. Mở XiaoAI > Cài đặt > Nhập API Key

### Sử dụng
- **Giọng nói**: Nhấn nút microphone hoặc giữ nút Home
- **Văn bản**: Nhập tin nhắn vào ô chat
- **Hình ảnh**: Nhấn nút đính kèm > Chọn ảnh từ thư viện
- **Video**: Nhấn nút đính kèm > Chọn video

## Cấu trúc project

```
XiaoAI/
├── app/
│   └── src/main/
│       ├── java/com/xiaoai/assistant/
│       │   ├── di/                 # Dependency Injection (Hilt)
│       │   ├── voice/              # Voice Interaction Services
│       │   ├── ui/                 # UI Components
│       │   │   ├── assistant/      # Main assistant screens
│       │   │   ├── onboarding/     # Onboarding flow
│       │   │   └── settings/       # Settings screens
│       │   ├── data/               # Data layer
│       │   │   ├── gemini/         # Gemini AI client
│       │   │   └── conversation/   # Conversation repository
│       │   ├── speech/             # Speech recognition & TTS
│       │   ├── media/              # Media handling
│       │   └── util/               # Utilities
│       └── res/                    # Resources
├── .github/workflows/              # GitHub Actions
└── build.gradle.kts                # Build configuration
```

## Công nghệ sử dụng

- **Kotlin** - Ngôn ngữ chính
- **Jetpack Compose** - UI Framework
- **Material Design 3** - Design system
- **Hilt** - Dependency Injection
- **Google Gemini AI** - AI Engine
- **Coroutines & Flow** - Async programming
- **DataStore** - Preferences storage

## License

MIT License - Xem file [LICENSE](LICENSE) để biết thêm chi tiết.

## Đóng góp

Mọi đóng góp đều được hoan nghênh! Vui lòng:
1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

## Liên hệ

Nếu có câu hỏi hoặc góp ý, vui lòng tạo Issue trên GitHub.
