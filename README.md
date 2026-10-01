# Wellio

Aplikasi Android untuk kebiasaan sehat yang sederhana. Kotlin, Jetpack Compose, Room, WorkManager. Minimum Android 8.0 (API 26).

## Menjalankan

Buka folder ini di Android Studio. Gunakan JDK 17 atau 21 dan Android SDK 36. Jalankan `./gradlew assembleDebug` atau tombol Run.

## Rencana implementasi

1. Fondasi Android dan desain dasar.
2. Model domain, penyimpanan Room, rencana lokal 7 hari.
3. Onboarding dan profil.
4. Today, status aktivitas, progres.
5. Sesi workout dan penggantian.
6. Detail dan penggantian meals.
7. Pengingat lokal.
8. Integrasi Firebase AI Logic opsional.
9. Validasi dan dokumentasi hasil.

Lihat `docs/CONCEPT.md` untuk konteks asli. File sumber berakhir di awal bagian 28; model profil diturunkan dari persyaratan onboarding. Git memakai identitas lokal `Wellio Project <wellio@localhost>` karena belum ada identitas Git pengguna yang dikonfigurasi. Tidak ada remote yang dibuat.
