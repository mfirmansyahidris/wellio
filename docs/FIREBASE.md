# Mengaktifkan Firebase AI Logic

Integrasi sudah ada dalam sumber dan ikut dikompilasi. Tanpa konfigurasi Firebase, aplikasi tetap bisa digunakan dalam mode lokal.

1. Buat/pilih Firebase project milik Anda. Daftarkan aplikasi Android dengan package `com.wellio.app`.
2. Aktifkan Firebase AI Logic menggunakan Gemini Developer API sesuai konsol Firebase.
3. Unduh `google-services.json` ke `app/google-services.json`. File ini diabaikan Git. Jangan menaruh Gemini API key dalam kode atau aset aplikasi.
4. Daftarkan App Check. Build debug menggunakan `DebugAppCheckProviderFactory`; daftarkan token debug dari perangkat uji di konsol, lalu jangan membagikan token tersebut. Build release menggunakan Play Integrity. Aktifkan enforcement Firebase AI Logic setelah konfigurasi App Check tervalidasi.
5. Pilih model yang tersedia di project Anda. Default kode adalah `gemini-3.8-flash`, sesuai daftar dukungan dokumentasi yang diperiksa saat pembuatan. Nama model bisa diubah melalui properti Gradle `wellioAiModel`, misalnya `./gradlew -PwellioAiModel=MODEL_YANG_DIAKTIFKAN assembleDebug`.
6. Jalankan ulang aplikasi. Buka **Profile → Use cloud AI**, baca penjelasan data, aktifkan, lalu pilih **Generate 7 days with AI**.
7. Uji generasi, penggantian meal/workout, penolakan output, offline, App Check, kuota, dan biaya di Firebase project Anda sebelum distribusi produksi.

## Perilaku integrasi

- `FirebasePlanner` mengirim satu permintaan untuk tujuh hari atau satu item pengganti.
- `Schemas` menentukan JSON schema. `PlanValidation` memeriksa jumlah/tanggal, durasi, peralatan, struktur latihan, bahan yang dihindari, dan tag pola makan.
- Nama pengguna dan riwayat aktivitas tidak dikirim. Preferensi dikodekan sebagai JSON; nilai bebas diperlakukan sebagai data.
- Timeout 90 detik. Tidak ada retry otomatis yang berpotensi menggandakan biaya.
- Respons yang gagal tidak mengganti data tersimpan. Perubahan status aktivitas di perangkat tetap merupakan operasi lokal.
- Pengguna dapat menonaktifkan AI dan melanjutkan rencana yang tersimpan.

## Sumber resmi yang digunakan

- [Firebase AI Logic overview](https://firebase.google.com/docs/ai-logic)
- [Structured output untuk Android](https://firebase.google.com/docs/ai-logic/generate-structured-output?platform=android)
- [App Check debug provider](https://firebase.google.com/docs/app-check/android/debug-provider)
- [App Check untuk AI Logic](https://firebase.google.com/docs/ai-logic/app-check)
- [Daftar model](https://firebase.google.com/docs/ai-logic/models)

Belum ada uji permintaan Gemini langsung karena file konfigurasi dan Firebase project pengguna tidak tersedia. Keberhasilan build tidak sama dengan keberhasilan konfigurasi cloud.
