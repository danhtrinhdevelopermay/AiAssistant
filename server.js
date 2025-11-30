const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 5000;

const html = `<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>XiaoAI - Android Virtual Assistant</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: #333;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
            padding: 40px 20px;
        }
        .hero {
            text-align: center;
            color: white;
            padding: 60px 20px;
        }
        .hero h1 {
            font-size: 3.5rem;
            margin-bottom: 20px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }
        .hero p {
            font-size: 1.3rem;
            opacity: 0.9;
            max-width: 600px;
            margin: 0 auto;
        }
        .logo {
            font-size: 80px;
            margin-bottom: 20px;
        }
        .card {
            background: white;
            border-radius: 16px;
            padding: 30px;
            margin: 20px 0;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }
        .card h2 {
            color: #6750A4;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .card h3 {
            color: #333;
            margin: 20px 0 10px;
        }
        .feature-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 20px;
            margin: 30px 0;
        }
        .feature {
            background: white;
            border-radius: 12px;
            padding: 25px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            transition: transform 0.3s, box-shadow 0.3s;
        }
        .feature:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }
        .feature-icon {
            font-size: 40px;
            margin-bottom: 15px;
        }
        .feature h3 {
            color: #6750A4;
            margin-bottom: 10px;
        }
        .code-block {
            background: #1e1e1e;
            color: #d4d4d4;
            padding: 20px;
            border-radius: 8px;
            overflow-x: auto;
            font-family: 'Consolas', 'Monaco', monospace;
            margin: 15px 0;
        }
        .code-block code {
            color: #9cdcfe;
        }
        .steps {
            counter-reset: step;
        }
        .step {
            display: flex;
            gap: 20px;
            margin: 20px 0;
            padding: 20px;
            background: #f8f8f8;
            border-radius: 12px;
        }
        .step-number {
            background: #6750A4;
            color: white;
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            flex-shrink: 0;
        }
        .btn {
            display: inline-block;
            background: #6750A4;
            color: white;
            padding: 15px 30px;
            border-radius: 30px;
            text-decoration: none;
            font-weight: 600;
            margin: 10px;
            transition: all 0.3s;
        }
        .btn:hover {
            background: #7F67BE;
            transform: scale(1.05);
        }
        .btn-outline {
            background: transparent;
            border: 2px solid white;
        }
        .btn-outline:hover {
            background: white;
            color: #6750A4;
        }
        .tech-stack {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin: 20px 0;
        }
        .tech-tag {
            background: #EADDFF;
            color: #6750A4;
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 0.9rem;
            font-weight: 500;
        }
        .warning {
            background: #FFF3E0;
            border-left: 4px solid #FF9800;
            padding: 15px 20px;
            border-radius: 0 8px 8px 0;
            margin: 20px 0;
        }
        .info {
            background: #E3F2FD;
            border-left: 4px solid #2196F3;
            padding: 15px 20px;
            border-radius: 0 8px 8px 0;
            margin: 20px 0;
        }
        footer {
            text-align: center;
            color: white;
            padding: 40px;
            opacity: 0.8;
        }
        @media (max-width: 600px) {
            .hero h1 { font-size: 2.5rem; }
            .logo { font-size: 60px; }
        }
    </style>
</head>
<body>
    <div class="hero">
        <div class="logo">🤖</div>
        <h1>XiaoAI</h1>
        <p>Trợ lý ảo thông minh cho Android - Thay thế hoàn hảo cho Google Assistant</p>
        <div style="margin-top: 30px;">
            <a href="#download" class="btn">Tải APK</a>
            <a href="#build" class="btn btn-outline">Hướng dẫn Build</a>
        </div>
    </div>

    <div class="container">
        <div class="feature-grid">
            <div class="feature">
                <div class="feature-icon">🎙️</div>
                <h3>Điều khiển giọng nói</h3>
                <p>Hỗ trợ nhận diện giọng nói tiếng Việt, phản hồi tự nhiên bằng Text-to-Speech</p>
            </div>
            <div class="feature">
                <div class="feature-icon">🖼️</div>
                <h3>Phân tích hình ảnh</h3>
                <p>Mô tả và trả lời câu hỏi về bất kỳ hình ảnh nào với Gemini AI</p>
            </div>
            <div class="feature">
                <div class="feature-icon">🎬</div>
                <h3>Phân tích video</h3>
                <p>Trích xuất và phân tích nội dung video thông minh</p>
            </div>
            <div class="feature">
                <div class="feature-icon">🔄</div>
                <h3>Thay thế Google Assistant</h3>
                <p>Đặt làm trợ lý mặc định, xuất hiện ở bất cứ đâu khi giữ nút Home</p>
            </div>
        </div>

        <div class="card" id="download">
            <h2>📥 Tải về và Cài đặt</h2>
            
            <div class="warning">
                <strong>⚠️ Lưu ý:</strong> Đây là ứng dụng Android native. Bạn cần push code lên GitHub để build APK tự động.
            </div>

            <h3>Cách 1: Build tự động với GitHub Actions</h3>
            <div class="steps">
                <div class="step">
                    <div class="step-number">1</div>
                    <div>
                        <strong>Push code lên GitHub</strong>
                        <p>Tạo repository mới và push toàn bộ code từ thư mục XiaoAI</p>
                    </div>
                </div>
                <div class="step">
                    <div class="step-number">2</div>
                    <div>
                        <strong>Cấu hình Secrets</strong>
                        <p>Vào Settings > Secrets > Actions và thêm: GEMINI_API_KEY</p>
                    </div>
                </div>
                <div class="step">
                    <div class="step-number">3</div>
                    <div>
                        <strong>Tải APK</strong>
                        <p>Vào Actions > Build XiaoAI APK > Artifacts để tải file APK</p>
                    </div>
                </div>
            </div>
        </div>

        <div class="card" id="build">
            <h2>🔧 Hướng dẫn Build thủ công</h2>
            
            <h3>Yêu cầu</h3>
            <div class="tech-stack">
                <span class="tech-tag">Android Studio</span>
                <span class="tech-tag">JDK 17</span>
                <span class="tech-tag">Android SDK 34</span>
                <span class="tech-tag">Gradle 8.2</span>
            </div>

            <h3>Các bước thực hiện</h3>
            <div class="code-block">
<code># 1. Clone repository
git clone &lt;repository-url&gt;
cd XiaoAI

# 2. Tạo file local.properties
echo "GEMINI_API_KEY=your_api_key_here" >> local.properties

# 3. Build APK
./gradlew assembleDebug

# APK sẽ nằm tại: app/build/outputs/apk/debug/</code>
            </div>
        </div>

        <div class="card">
            <h2>🔑 Lấy Gemini API Key</h2>
            <div class="steps">
                <div class="step">
                    <div class="step-number">1</div>
                    <div>
                        <strong>Truy cập Google AI Studio</strong>
                        <p>Vào <a href="https://aistudio.google.com" target="_blank">aistudio.google.com</a></p>
                    </div>
                </div>
                <div class="step">
                    <div class="step-number">2</div>
                    <div>
                        <strong>Đăng nhập Google</strong>
                        <p>Sử dụng tài khoản Google của bạn</p>
                    </div>
                </div>
                <div class="step">
                    <div class="step-number">3</div>
                    <div>
                        <strong>Tạo API Key</strong>
                        <p>Click "Get API Key" > "Create API key in new project"</p>
                    </div>
                </div>
            </div>
        </div>

        <div class="card">
            <h2>📱 Cách sử dụng XiaoAI</h2>
            
            <h3>Đặt làm trợ lý mặc định</h3>
            <div class="info">
                Vào <strong>Cài đặt > Ứng dụng > Ứng dụng mặc định > Trợ lý kỹ thuật số</strong> và chọn XiaoAI
            </div>

            <h3>Gọi XiaoAI</h3>
            <ul style="margin: 15px 0 15px 20px;">
                <li>Giữ nút Home để mở XiaoAI</li>
                <li>Nhấn nút microphone để nói</li>
                <li>Hoặc nhập tin nhắn trực tiếp</li>
            </ul>

            <h3>Gửi hình ảnh/video</h3>
            <ul style="margin: 15px 0 15px 20px;">
                <li>Nhấn nút đính kèm (+)</li>
                <li>Chọn ảnh hoặc video từ thư viện</li>
                <li>Đặt câu hỏi về nội dung</li>
            </ul>
        </div>

        <div class="card">
            <h2>🛠️ Công nghệ sử dụng</h2>
            <div class="tech-stack">
                <span class="tech-tag">Kotlin</span>
                <span class="tech-tag">Jetpack Compose</span>
                <span class="tech-tag">Material Design 3</span>
                <span class="tech-tag">Hilt DI</span>
                <span class="tech-tag">Google Gemini AI</span>
                <span class="tech-tag">VoiceInteractionService</span>
                <span class="tech-tag">Coroutines & Flow</span>
                <span class="tech-tag">DataStore</span>
            </div>
        </div>

        <div class="card">
            <h2>📁 Cấu trúc Project</h2>
            <div class="code-block">
<code>XiaoAI/
├── app/src/main/java/com/xiaoai/assistant/
│   ├── di/           # Dependency Injection (Hilt)
│   ├── voice/        # VoiceInteraction Services
│   ├── ui/           # Jetpack Compose UI
│   │   ├── assistant/  # Main assistant screens
│   │   ├── onboarding/ # Onboarding flow
│   │   └── settings/   # Settings screens
│   ├── data/         # Data layer
│   │   ├── gemini/   # Gemini AI client
│   │   └── conversation/ # Chat repository
│   ├── speech/       # Speech Recognition & TTS
│   └── media/        # Image/Video processing
├── .github/workflows/  # GitHub Actions
└── build.gradle.kts    # Build configuration</code>
            </div>
        </div>
    </div>

    <footer>
        <p>XiaoAI - Trợ lý ảo thông minh cho Android</p>
        <p style="margin-top: 10px;">Powered by Google Gemini AI</p>
    </footer>
</body>
</html>`;

const server = http.createServer((req, res) => {
    res.writeHead(200, { 
        'Content-Type': 'text/html; charset=utf-8',
        'Cache-Control': 'no-cache'
    });
    res.end(html);
});

server.listen(PORT, '0.0.0.0', () => {
    console.log('XiaoAI Documentation Server running at http://0.0.0.0:' + PORT);
    console.log('This is the documentation page for the XiaoAI Android app.');
    console.log('The actual Android app is in the XiaoAI/ directory.');
});
