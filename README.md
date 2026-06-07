# 🌤 WeatherNow

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="96" alt="WeatherNow Logo"/>
</p>

<p align="center">
  Ứng dụng thời tiết Android hiện đại – tra cứu thời tiết theo tên thành phố hoặc vị trí GPS, với dự báo theo giờ và dự báo 5 ngày.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen?logo=android" />
  <img src="https://img.shields.io/badge/Language-Java-orange?logo=java" />
  <img src="https://img.shields.io/badge/API-OpenWeatherMap-blue" />
  <img src="https://img.shields.io/badge/UI-Dark%20Mode-1a1a2e" />
</p>

---

## 📱 Tính năng

| Tính năng | Mô tả |
|-----------|-------|
| 🔍 **Tìm kiếm thành phố** | Nhập tên thành phố bất kỳ trên thế giới |
| 📍 **Định vị GPS** | Tự động lấy thời tiết theo vị trí hiện tại |
| 🌡 **Thời tiết hiện tại** | Nhiệt độ, mô tả, cảm giác, độ ẩm, gió, tầm nhìn |
| 🌅 **Mặt trời mọc/lặn** | Giờ mặt trời mọc và lặn theo múi giờ địa phương |
| ⏱ **Dự báo theo giờ** | 40 mốc thời tiết (mỗi 3h, trong 5 ngày) |
| 📅 **Dự báo 5 ngày** | 1 item đại diện mỗi ngày với min/max/humidity/pop |
| 🌡 **Chuyển đổi °C/°F** | Toggle giữa Celsius và Fahrenheit tức thì |
| 💾 **Ghi nhớ thành phố** | Tự động tải lại thành phố cuối khi mở app |
| 🔄 **Swipe to Refresh** | Kéo xuống để cập nhật dữ liệu |

---

## 🖼 Giao diện

- **Dark Mode** với gradient nền `#0F0C29 → #302B63 → #24243E`
- **Glassmorphism cards** – hiệu ứng kính mờ trong suốt
- **Status bar trong suốt** – full screen immersive
- **Icon thời tiết động** – load từ OpenWeatherMap CDN bằng Glide

---

## 🏗 Kiến trúc dự án

```
com.re.weathernow/
├── activities/
│   └── MainActivity.java          ← Màn hình chính duy nhất
├── adapter/
│   ├── ForecastAdapter.java       ← Danh sách dự báo 5 ngày
│   └── HourlyAdapter.java         ← Danh sách dự báo theo giờ
├── api/
│   ├── ApiClient.java             ← Singleton Retrofit client
│   └── WeatherService.java        ← Interface REST endpoints
├── model/
│   ├── CurrentWeatherResponse.java ← Model thời tiết hiện tại
│   └── ForecastResponse.java       ← Model dự báo
└── utils/
    └── Constants.java             ← Hằng số toàn cục
```

---

## 🔧 Công nghệ sử dụng

| Thư viện | Phiên bản | Mục đích |
|---------|-----------|---------|
| [Retrofit2](https://square.github.io/retrofit/) | 2.x | HTTP client gọi REST API |
| [Gson Converter](https://github.com/square/retrofit/tree/master/retrofit-converters/gson) | — | Parse JSON tự động |
| [Glide](https://github.com/bumptech/glide) | 4.x | Load & cache ảnh icon |
| [Google Play Services Location](https://developers.google.com/location-context) | — | FusedLocationProviderClient |
| AndroidX SwipeRefreshLayout | — | Kéo để làm mới |
| AndroidX RecyclerView | — | Danh sách hiệu năng cao |

---

## 🌐 API

Dự án sử dụng **[OpenWeatherMap API](https://openweathermap.org/api)** (miễn phí):

| Endpoint | Mô tả |
|----------|-------|
| `GET /data/2.5/weather?q={city}` | Thời tiết hiện tại theo tên thành phố |
| `GET /data/2.5/weather?lat={lat}&lon={lon}` | Thời tiết hiện tại theo tọa độ GPS |
| `GET /data/2.5/forecast?q={city}` | Dự báo 5 ngày theo tên thành phố |
| `GET /data/2.5/forecast?lat={lat}&lon={lon}` | Dự báo 5 ngày theo tọa độ GPS |

> **Base URL:** `https://api.openweathermap.org/data/2.5/`

---

## ⚙️ Cài đặt & Chạy dự án

### Yêu cầu
- Android Studio Hedgehog (2023.1.1) trở lên
- JDK 11+
- Android SDK API 24+
- Kết nối Internet

### Bước 1: Clone dự án
```bash
git clone https://github.com/your-username/WeatherNow.git
cd WeatherNow
```

### Bước 2: Cấu hình API Key
Mở file `app/src/main/java/com/re/weathernow/utils/Constants.java` và thay thế API key:
```java
public static final String API_KEY = "YOUR_OPENWEATHERMAP_API_KEY";
```

> Đăng ký API key miễn phí tại: https://openweathermap.org/api

### Bước 3: Build & Run
```bash
./gradlew assembleDebug
```
Hoặc nhấn **Run ▶** trong Android Studio.

---

## 📋 Quyền hệ thống

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

- **INTERNET** – bắt buộc để gọi API
- **ACCESS_FINE_LOCATION** – xin runtime khi dùng tính năng GPS
- **ACCESS_COARSE_LOCATION** – định vị mạng xấp xỉ

---

## 📂 Resources chính

| File | Mô tả |
|------|-------|
| `res/layout/activity_main.xml` | Layout màn hình chính (433 dòng) |
| `res/layout/item_hourly.xml` | Card dự báo theo giờ (80dp width) |
| `res/layout/item_forecast.xml` | Card dự báo 5 ngày (2-row layout) |
| `res/drawable/bg_gradient.xml` | Nền gradient tím đậm |
| `res/drawable/card_glass.xml` | Card glassmorphism |
| `res/drawable/hourly_card_bg.xml` | Background card hourly |
| `res/drawable/forecast_item_bg.xml` | Background item forecast |
| `res/values/colors.xml` | Bảng màu toàn dự án |

---

## 🗂 Cấu trúc dữ liệu thời tiết

### Thời tiết hiện tại
```
CurrentWeatherResponse
├── cityName          ← Tên thành phố
├── main.temp         ← Nhiệt độ
├── main.feelsLike    ← Cảm giác nhiệt
├── main.humidity     ← Độ ẩm (%)
├── main.tempMin/Max  ← Nhiệt độ biên
├── weather[].description  ← Mô tả
├── weather[].icon    ← Mã icon
├── wind.speed        ← Tốc độ gió (m/s)
├── visibility        ← Tầm nhìn (mét)
├── sys.country       ← Mã quốc gia
├── sys.sunrise/sunset ← Unix timestamp
└── timezone          ← UTC offset (giây)
```

### Dự báo (40 mốc × 3h = 5 ngày)
```
ForecastResponse.list[]
├── dateTime          ← "yyyy-MM-dd HH:mm:ss"
├── main.temp/humidity/tempMin/tempMax
├── weather[].description/icon
├── wind.speed
└── pop               ← Xác suất mưa (0.0–1.0)
```

---

## 👥 Đóng góp

Dự án được phát triển theo mô hình nhóm:

- **Person 1** – Models & API integration
- **Person 2** – UI Components & Forecast adapters
- **Person 3** – Main Activity & Business logic

---

## 📄 License

```
MIT License – Tự do sử dụng cho mục đích học tập và nghiên cứu.
```

---
