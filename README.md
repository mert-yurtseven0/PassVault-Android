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
