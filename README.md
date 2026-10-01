# Wellio

**Small steps. A healthier you.** Aplikasi Android wellness berdasarkan konteks yang diberikan: rencana harian, gerak ringan, ide makanan, rutinitas, pengingat, dan progres sederhana.

Android application ID dan namespace: `com.fi.wellio`.

## Jalankan proyek

1. Buka direktori `wellio` di Android Studio.
2. Gunakan JDK 17 atau 21, Android SDK 36, dan perangkat/emulator Android 8.0 (API 26) atau lebih baru.
3. Biarkan Android Studio membuat `local.properties`, lalu tekan **Run**.

Dari terminal dengan `JAVA_HOME` dan `ANDROID_HOME` yang sesuai:

```sh
./gradlew assembleDebug
./gradlew testDebugUnitTest lintDebug
./gradlew connectedDebugAndroidTest
```

APK debug berada di `app/build/outputs/apk/debug/app-debug.apk`. Gradle Wrapper disertakan. Versi dependensi dipatok agar build dapat direproduksi; proyek tidak mengklaim memakai versi paling baru.

## Fitur yang tersedia

- Onboarding singkat: maksimal dua goal, aktivitas, pengalaman, waktu, lokasi, peralatan, pola makan, cuisine, prioritas, dan makanan yang dihindari.
- Rencana lokal tujuh hari tanpa akun atau koneksi internet setelah aplikasi terpasang. Seluruh preferensi tersimpan; katalog lokal memprioritaskan batasan makanan, cuisine, kecepatan persiapan, dan protein/sayur sesuai pilihan.
- Empat tujuan navigasi: **Today, Workout, Meals, Profile**.
- Status aktivitas pending, completed, skipped, rescheduled; jam dapat diubah dalam hari yang dipilih.
- Sesi workout per set, bisa dijeda dan dilanjutkan setelah aplikasi dibuka ulang. Durasi adalah perkiraan, bukan stopwatch atau target yang wajib dipenuhi.
- Penggantian workout berdasarkan alasan, serta penggantian satu meal tanpa mengubah meal lainnya.
- Bahan dan langkah memasak sederhana; tanpa calorie counting.
- Pengingat per aktivitas dengan izin notifikasi Android dan WorkManager. Waktu pengiriman bersifat perkiraan. Jam yang sudah lewat tidak membuat pengingat baru.
- Progres mingguan, persentase aktivitas selesai, streak, dan riwayat lokal. Satu aktivitas selesai per hari mempertahankan streak; skip tidak menambah progres.
- Firebase AI Logic opsional: generasi tujuh hari, penggantian workout, dan penggantian meal. Respons JSON divalidasi sebelum disimpan.

## Mode lokal dan AI

Aplikasi langsung berjalan dalam **mode lokal**, tanpa `google-services.json`. Ini generator berbasis katalog dan aturan, bukan hasil AI. UI menampilkan sumber rencana dengan jelas.

Untuk menghubungkan AI milik Anda, ikuti [panduan Firebase](docs/FIREBASE.md). Tidak ada kredensial atau proyek cloud yang dibuat oleh implementasi ini. Setelah tersambung, pengguna mengaktifkan AI lewat Profile dengan penjelasan data yang dikirim. Tidak ada pemanggilan AI diam-diam setiap membuka aplikasi. Error, timeout, atau output tidak valid mempertahankan rencana yang ada.

## Data dan perilaku

Data disimpan di Room pada perangkat. Tidak ada login, sinkronisasi, atau analytics. Cloud AI hanya menerima preferensi perencanaan dan konten yang ingin diganti, tanpa nama atau riwayat penyelesaian. Menonaktifkan AI menghentikan permintaan berikutnya; data yang sudah dikirim tunduk pada layanan Firebase/Google yang dikonfigurasi pemilik proyek.

Pembaruan profil menyegarkan aktivitas belum selesai; aktivitas selesai, jadwal yang sudah diubah, dan riwayat tetap ada. Hari yang belum tersedia dibuat lokal ketika aplikasi dibuka. Jika katalog tidak mencakup preferensi AI yang tersimpan, aplikasi mengulang konten tervalidasi dari rencana terakhir dan menandainya **Saved plan repeat**. Generasi AI mingguan baru dijalankan lewat tombol pengguna.

## Batas MVP

- UI menggunakan bahasa Inggris sesuai contoh konsep; dokumentasi proyek berbahasa Indonesia.
- Katalog lokal terbatas. Tidak semua alat yang dipilih dipakai dalam setiap workout; pilihan alat membatasi apa yang boleh digunakan. Personalisasi lebih luas tersedia melalui integrasi AI.
- Reschedule mengubah waktu pada tanggal yang dipilih, bukan memindahkan aktivitas lintas tanggal.
- Makanan yang dihindari merupakan preferensi, bukan penyaring alergi medis. Resep tidak menyediakan gram, porsi, atau hitungan nutrisi.
- Bukan layanan medis, diagnosis, atau terapi. Tidak ada wearable, video workout, cloud sync, atau akun pada iterasi ini.
- APK yang disediakan adalah build debug untuk pengujian, belum ditandatangani untuk distribusi produksi.

## Riwayat pengembangan

Setiap tahap logis memiliki commit terpisah. Gunakan `git log --oneline --reverse` untuk menelusuri fondasi → model → onboarding → Today → workout → meals → reminder → AI → validasi/perbaikan. Tidak ada squash, amend, remote, atau push.

Identitas Git lokal: `Wellio Project <wellio@localhost>`, karena identitas pengguna belum tersedia saat proyek dibuat. Ubah melalui pengaturan Git lokal untuk commit berikutnya bila diperlukan.

- [Konsep asli](docs/CONCEPT.md)
- [Keputusan arsitektur](docs/ARCHITECTURE.md)
- [Konfigurasi Firebase](docs/FIREBASE.md)
- [Catatan verifikasi](docs/VALIDATION.md)

File konsep yang diterima berakhir pada awal bagian 28 (`UserProfile`); atribut profil dilengkapi dari persyaratan onboarding di bagian sebelumnya.
