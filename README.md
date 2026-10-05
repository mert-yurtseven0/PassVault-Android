# 🤖 PassVault Android - Güvenli ve Çevrimdışı Şifre Yöneticisi (.apk)

**PassVault Android**, iOS sürümü ile birebir aynı güvenlik protokollerine (**AES-256-GCM**, **PBKDF2-HMAC-SHA256**, **BiometricPrompt**, **RFC 6238 TOTP**) sahip; sıfır bilgi (*Zero-Knowledge*) prensibiyle çalışan yerel Android (Kotlin & Jetpack Compose) uygulamasıdır.

---

## 🔒 Güvenlik Özellikleri

* **AES-256-GCM Şifreleme**: Tüm şifreler cihazınızın yerel depolama alanında donanım seviyesinde AES-GCM ile şifrelenir.
* **PBKDF2 Anahtar Türetme (100.000 İterasyon)**: Ana şifreniz hiçbir zaman ham haliyle saklanmaz.
* **BiometricPrompt (Parmak İzi & Yüz Tanıma)**: Android donanım destekli biyometrik kilit.
* **FLAG_SECURE Koruması**: Ekran görüntüsü alınamaz ve son kullanılan uygulamalar (*Recent Apps / Overview*) ekranında şifreleriniz sansürlenir.
* **RFC 6238 TOTP 2FA**: Google Authenticator gerektirmeden 30 saniyelik iki adımlı doğrulama kodları.
* **Otomatik Pano Temizleme**: Kopyalanan şifreler 30 saniye sonra Android panosundan otomatik temizlenir.
* **Sıfır İnternet İzni**: Uygulama `AndroidManifest.xml` içinde internet izni (`INTERNET`) **bulundurmaz**. Tamamen çevrimdışıdır (Air-Gapped).

---

## 📦 APK Dosyası Nasıl Üretilir?

Android APK dosyası oluşturmak için aşağıdaki 2 kolay yöntemden birini kullanabilirsiniz:

### Yöntem 1: Android Studio ile (En Kolay)
1. Mac'inize ücretsiz [Android Studio](https://developer.android.com/studio) indirin ve kurun.
2. Android Studio'yu açıp **Open** deyin ve şu klasörü seçin:
   ```
   /Users/merttalip/.gemini/antigravity/scratch/PassVault-Android
   ```
3. Üst menüden **Build** -> **Build Bundle(s) / APK(s)** -> **Build APK(s)** seçeneğine tıklayın.
4. Birkaç saniye içinde derleme bitecek ve sağ altta beliren **locate** bağlantısına tıkladığınızda `app-debug.apk` dosyanız hazır olacaktır!

### Yöntem 2: Sıfır Kurulum - GitHub Actions ile Bulutta (1 Dakikada)
Projede hazır bir GitHub Actions iş akışı (`.github/workflows/build-apk.yml`) bulunmaktadır:
1. Bu klasörü bir GitHub deposuna yükleyin (Push).
2. GitHub'da **Actions** sekmesine gidin.
3. Otomatik olarak başlayan build tamamlandığında **Artifacts** kısmından `PassVault-debug-apk` dosyasını doğrudan telefonunuza indirin!

---

## 📲 APK'yı Android Telefona Yükleme

1. Üretilen `app-debug.apk` dosyasını telefonunuza aktarın (USB kablo, WhatsApp, Telegram, Google Drive veya Bluetooth ile).
2. Telefonda dosyaya dokunun.
3. *"Bilinmeyen kaynaklardan yüklemeye izin ver"* uyarısı çıkarsa onaylayın ve **Yükle** butonuna basın.
4. PassVault telefonunuzda kullanıma hazır!

---

## 📂 Proje Yapısı

```
PassVault-Android/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/merttalip/passvault/
│       │   ├── MainActivity.kt
│       │   ├── models/ (VaultItem, VaultData)
│       │   ├── security/ (CryptoManager, BiometricHelper, TOTPManager, PasswordGenerator)
│       │   ├── services/ (VaultStore)
│       │   └── ui/ (Screens & Compose Theme)
│       └── res/ (Strings, Colors, Themes)
├── build.gradle.kts
├── settings.gradle.kts
├── .github/workflows/build-apk.yml
└── README.md
```
