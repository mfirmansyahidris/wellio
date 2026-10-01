# Verifikasi implementasi

Dijalankan pada 1 Oktober 2026 di lingkungan pengembangan lokal.

## Hasil

| Pemeriksaan | Hasil |
| --- | --- |
| `assembleDebug` | Lulus; APK debug berhasil dibangun |
| `compileReleaseKotlin` | Lulus; termasuk provider App Check untuk release |
| `testDebugUnitTest` | 14 pengujian lulus, 0 kegagalan |
| `lintDebug` | Lulus tanpa error; peringatan ketersediaan versi dependensi baru tetap dicatat |
| Instrumentation pada emulator Android 15 / API 35 ARM64 | 4 pengujian lulus, 0 kegagalan, sekitar 30 detik |

Pengujian perangkat dijalankan dengan APK aplikasi dan APK AndroidTest yang terpasang pada emulator khusus pengembangan. Perangkat lain tidak dipakai.

## Yang diperiksa

- Tujuh tanggal unik, empat meal per hari, serta ID aktivitas yang konsisten.
- Preferensi vegan, daftar makanan yang dihindari, penggantian item, dan penolakan saat katalog tidak menyediakan kandidat.
- Progres, streak, skip, serialisasi untuk penyimpanan, dan pelestarian aktivitas selesai saat refresh.
- Validasi keluaran AI: tanggal duplikat, alat yang tidak tersedia, jumlah set tidak wajar, bahan yang dihindari, serta klaim tag vegan yang tidak cocok dengan bahan.
- Transaksi Room bersamaan tidak menghilangkan perubahan.
- UI onboarding → Today → completion → activity recreation → meal replacement → workout sets → Profile.
- UI jam tidak valid, reschedule, skip, dan reset ke pending.
- WorkManager mempertahankan permintaan yang sama ketika rekonsiliasi diulang, menggantinya saat jadwal berubah, dan membatalkannya saat aktivitas selesai.
- Pemeriksaan visual layar welcome pada emulator; alur Today juga diperiksa oleh pengujian UI.

## Batas hasil uji

- Belum mengirim permintaan Gemini langsung karena Firebase project, `google-services.json`, dan token App Check pengguna tidak disediakan.
- Pengiriman notifikasi pada perangkat fisik, pembatasan baterai vendor, reboot, serta perubahan zona waktu belum diuji end-to-end. Penjadwalan dan pembatalan WorkManager diuji di emulator.
- Tidak ada build release bertanda tangan, publikasi Play Store, audit aksesibilitas menyeluruh, atau klaim kesiapan produksi.
- Perbaikan terakhir pada pesan timeout cloud dan penjelasan refresh profil AI telah dikompilasi dan diperiksa lint; koneksi cloud tetap perlu diuji setelah konfigurasi.
