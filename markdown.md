# ReWear ♻️📱 — Sustainable Fashion & AI Wardrobe Assistant

**ReWear** adalah aplikasi Android inovatif berbasis **Jetpack Compose** dan **Kotlin** yang dirancang untuk mendukung gerakan fesyen berkelanjutan (*sustainable fashion*). Aplikasi ini membantu pengguna melacak dampak lingkungan dari lemari pakaian (*wardrobe*) mereka, memanfaatkan kecerdasan buatan (**Gemini AI**) untuk analisis pakaian dan rekomendasi *outfit*, serta menyediakan fasilitas untuk sirkulasi pakaian melalui fitur tukar-menukar (*swap*) dan donasi sosial.

---

## 🎯 Latar Belakang & Tujuan Proyek

Industri fesyen cepat (*fast fashion*) merupakan salah satu penyumbang emisi karbon (CO₂) dan limbah air terbesar di dunia. Pakaian yang menumpuk di lemari dan berakhir di TPA (Tempat Pembuangan Akhir) memperburuk kerusakan lingkungan.

**ReWear** hadir untuk menyelesaikan masalah ini dengan:
1. **Meningkatkan Kesadaran Lingkungan**: Memberikan kalkulasi nyata atas konsumsi air (*Water Footprint*) dan emisi karbon (*Carbon Footprint*) dari setiap kain yang dimiliki pengguna berdasarkan studi *Life Cycle Assessment* (LCA).
2. **Memaksimalkan Penggunaan Pakaian**: Mengkampanyekan tantangan pakai ulang seperti `#30Wears` untuk memperpanjang masa hidup pakaian.
3. **Mendorong Ekonomi Sirkular**: Mempermudah proses pertukaran pakaian pra-loved (*swap*) dan donasi pakaian ke yayasan sosial terverifikasi.

---

## ✨ Fitur Utama Aplikasi

### 1. Dashboard Dampak Ekologis (Eco Dashboard)
*   **Eco Score Ring**: Cincin skor ekologis yang dinamis dengan animasi visual yang menunjukkan seberapa ramah lingkungan lemari pakaian pengguna (berkisar antara 0 - 100).
*   **Environmental Statistics**: Metrik real-time yang menghitung total pakaian, estimasi emisi CO₂ yang berhasil dikurangi/terselamatkan, serta konsumsi air dalam liter.
*   **Personalized Eco Tips**: Sistem cerdas yang memberikan tips ramah lingkungan secara acak dan dinamis berdasarkan tipe serat kain yang dimiliki pengguna. Contohnya:
    *   *Kain Alami (Katun/Linen)*: Menyarankan pembersihan noda secara lokal (*spot-cleaning*) dibanding pencucian mesin skala penuh untuk menghemat air.
    *   *Kain Sintetis (Polyester/Nylon)*: Menyarankan pemakaian kantong cucian khusus guna menangkap mikroplastik berbahaya agar tidak mencemari laut.
    *   *Frekuensi Pakai*: Mengingatkan tantangan `#30Wears` jika rata-rata pemakaian baju di bawah 5 kali.

### 2. Lemari Pakaian Digital (Digital Wardrobe)
*   Mengatalogkan seluruh koleksi pakaian secara rapi dan tersimpan secara lokal menggunakan database **SQLite Room**.
*   Menyimpan parameter pakaian seperti: nama, kategori (Tops, Bottoms, Dresses, Outerwear, Shoes, Accessories), jenis serat kain, berat (kg), foto, jumlah pemakaian, dan tanggal ditambahkan.
*   Menghitung emisi CO₂ dan air per item pakaian secara otomatis berdasarkan berat dan jenis kain.

### 3. Pemindaian Cerdas (Smart Scan) dengan Gemini Vision AI
*   Membuka kamera perangkat menggunakan **CameraX API** untuk memotret pakaian.
*   Foto dikirim ke model **Gemini Flash Lite (`gemini-flash-lite-latest`)** untuk dievaluasi secara multimodal.
*   AI secara otomatis mendeteksi:
    *   Nama pakaian (dalam bahasa Indonesia, contoh: *"Celana Jeans Biru"*).
    *   Kategori pakaian yang paling pas.
    *   Jenis serat kain yang dominan.
    *   Estimasi berat pakaian (kg).
*   Data hasil prediksi AI langsung mengisi formulir penambahan pakaian secara otomatis (*auto-populate*), menghemat waktu input pengguna.

### 4. Rekomendasi Outfit Pintar (AI Stylist)
*   Menganalisis isi lemari pakaian digital pengguna dengan mengirimkan daftar baju beserta visual gambarnya ke model **Gemini Flash Lite**.
*   AI bertindak sebagai penata gaya pribadi (*sustainable stylist*) dan menghasilkan 3 rekomendasi kombinasi pakaian yang trendi dari lemari pakaian yang ada.
*   Setiap rekomendasi dilengkapi dengan:
    *   Daftar item pakaian yang digunakan.
    *   Estimasi jumlah CO₂ yang berhasil diselamatkan (dibanding membeli baju baru).
    *   *Style Note* edukatif (contoh: *"Dengan memilih padu padan ini daripada membeli baru, Anda menghemat air setara dengan 10 kali mandi!"*).

### 5. Pasar Swap Pakaian (Swap Marketplace)
*   Pengguna dapat menandai pakaian yang ingin ditukar atau dijual dengan menandai bendera `isForSwap = true`.
*   Tersedia fitur bagikan detail baju secara otomatis (*auto-text sharing*) ke platform marketplace eksternal (seperti OLX, Carousell, atau media sosial) untuk memfasilitasi transaksi fesyen sirkular.

### 6. Donasi Sosial Pakaian (Charity Donation)
*   Pakaian yang sudah tidak dipakai bisa disalurkan ke yayasan sosial terdaftar dengan mengubah statusnya menjadi `isForDonation = true`.
*   Terintegrasi dengan basis data yayasan sosial yang menampilkan nama yayasan, kota, alamat lengkap pengiriman, serta instruksi *shipping* yang mudah.

### 7. Panduan Serat Kain (Fabric Guide)
*   Katalog edukatif lengkap tentang jenis serat alami (katun, linen, wol, sutra, rami, bambu) dan sintetis (polyester, nilon, akrilik, spandeks, fleece) beserta rincian nilai dampak lingkungannya (CO₂ per kg dan Air per kg).

---

## 🛠️ Arsitektur & Teknologi Stack

Aplikasi ini menggunakan pola arsitektur **MVVM (Model-View-ViewModel)** dengan struktur kode modern berbasis komponen deklaratif Jetpack Compose.

| Komponen | Teknologi / Pustaka |
| :--- | :--- |
| **Bahasa Utama** | Kotlin |
| **Framework UI** | Jetpack Compose (Material 3) |
| **Penyimpanan Lokal** | Room Database (SQLite) dengan Kotlin Symbol Processing (KSP) |
| **Kecerdasan Buatan (AI)** | Google Generative AI Android SDK (`gemini-flash-lite-latest`) |
| **Layanan Cloud / Auth** | Firebase Authentication & Cloud Firestore |
| **Kamera & Gambar** | CameraX API & Coil Compose (Image Loading) |
| **Koneksi Jaringan (HTTP)**| OkHttp 4.12.0 (digunakan dalam `ClaudeApiService` untuk opsi integrasi legacy) |
| **Versi SDK Target** | Compile & Target SDK 36, Min SDK 24 |

---

## 📂 Struktur Direktori Utama

Berikut adalah organisasi file di dalam paket `com.damtoy.rewear`:

```text
rewear/
│
├── MainActivity.kt                # Titik masuk utama aplikasi (Activity)
├── ReWearApp.kt                   # Inisialisasi basis data Room & Firebase App
│
├── data/                          # Lapisan Penyimpanan Lokal (Room Database)
│   ├── AppDatabase.kt             # Konfigurasi database Room
│   ├── ClothingDao.kt             # Query database untuk pakaian
│   └── Converters.kt              # Converter tipe untuk Date & Enum
│
├── domain/                        # Logika Bisnis Utama
│   └── CarbonCalculator.kt        # Perhitungan dampak ekologis & rekomendasi tips
│
├── model/                         # Definisi Data Model / Entitas
│   ├── ClothingItem.kt            # Entity utama pakaian untuk Room
│   ├── ClothingCategory.kt        # Enum kategori pakaian (Tops, Bottoms, dll.)
│   ├── FabricType.kt              # Enum jenis kain beserta data konstanta LCA CO2 & Air
│   ├── UserProfile.kt             # Data profil pengguna & Eco Score
│   └── Yayasan.kt                 # Data yayasan untuk program donasi
│
├── network/                       # Integrasi Layanan Eksternal & API AI
│   ├── GeminiVisionService.kt     # Integrasi Gemini Vision untuk klasifikasi foto pakaian
│   ├── GeminiOutfitService.kt     # Integrasi Gemini Multimodal untuk saran outfit
│   └── ClaudeApiService.kt        # Opsi integrasi API Anthropic Claude via OkHttp
│
├── repository/                    # Lapisan Repositori (Sumber Data Tunggal)
│   ├── ClothingRepository.kt      # Repositori pengelolaan data pakaian
│   ├── UserRepository.kt          # Repositori data profil pengguna
│   └── YayasanRepository.kt       # Repositori daftar yayasan penerima donasi
│
└── ui/                            # Lapisan Antarmuka Pengguna (UI)
    ├── ReWearAppContent.kt        # Penanganan NavHost & Bottom Navigation
    ├── theme/                     # Konfigurasi tema Material 3 (Color, Typography, Shape, dll.)
    │
    # Sub-fitur berdasarkan fungsionalitas UI:
    ├── auth/                      # UI Registrasi & Login (Firebase Auth)
    ├── dashboard/                 # UI Dashboard utama & statistik dampak lingkungan
    ├── wardrobe/                  # UI Daftar lemari pakaian digital pengguna
    ├── addclothing/               # UI Formulir input pakaian & scanner kamera AI
    ├── detail/                    # UI Detail pakaian & kalkulasi dampak per-item
    ├── outfit/                    # UI Rekomendasi outfit ramah lingkungan dari AI
    ├── swap/                      # UI Marketplace tukar pakaian & tab Donasi
    ├── donate/                    # UI Pengelolaan donasi pakaian ke yayasan sosial
    ├── profile/                   # UI Profil pengguna & preferensi pakaian
    ├── guide/                     # UI Panduan edukasi serat kain
    └── splash/                    # UI Splash screen animasi pembuka
```

---

## 🔑 Konfigurasi & Cara Instalasi

### 1. Prasyarat Sistem
*   Android Studio Jellyfish (atau versi di atasnya).
*   Perangkat Android fisik atau Emulator dengan API level minimum **24 (Android 7.0+)**.
*   Koneksi internet untuk autentikasi Firebase dan pemrosesan AI Gemini.

### 2. Pengaturan API Key Gemini
Untuk menggunakan fitur analisis gambar dan rekomendasi baju AI, Anda harus menyiapkan API Key Google AI Studio.
1. Dapatkan API Key di [Google AI Studio](https://aistudio.google.com/).
2. Buka berkas `local.properties` di direktori utama proyek Anda.
3. Tambahkan baris berikut di bagian akhir berkas:
   ```properties
   GEMINI_API_KEY=MASUKKAN_API_KEY_ANDA_DI_SINI
   ```
4. Gradle akan membaca kunci ini secara otomatis saat build dan menyuntikkannya ke `BuildConfig.GEMINI_API_KEY`.

### 3. Pengaturan Firebase
Aplikasi ini menggunakan **Firebase Auth** untuk pendaftaran pengguna.
1. Buat proyek baru di [Firebase Console](https://console.firebase.google.com/).
2. Daftarkan aplikasi Android dengan nama paket `com.damtoy.rewear`.
3. Unduh berkas `google-services.json` dan letakkan di dalam folder `app/` proyek Anda.
4. Aktifkan metode login **Email/Password** di tab Authentication Firebase.

### 4. Build dan Run
1. Buka proyek ReWear di Android Studio.
2. Tunggu proses sinkronisasi Gradle selesai.
3. Klik tombol **Run 'app'** untuk menginstal aplikasi pada emulator atau perangkat Android Anda.

---

## 🌿 Kontribusi terhadap Pembangunan Berkelanjutan
Melalui program digitalisasi pakaian, ReWear berupaya mendukung tujuan global konsumsi dan produksi yang bertanggung jawab (**SDG 12: Responsible Consumption and Production**) serta penanganan perubahan iklim (**SDG 13: Climate Action**). Dengan ReWear, setiap pakaian berhak mendapatkan kesempatan hidup kedua! 👕♻️
