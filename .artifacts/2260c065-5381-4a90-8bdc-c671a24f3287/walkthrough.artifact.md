# Walkthrough - Database Cleanup & Reset

Fitur pembersihan data telah ditambahkan untuk memungkinkan penghapusan produk "lama" yang sebelumnya terkunci oleh riwayat transaksi.

## Perubahan yang Dilakukan

1.  **Database Nuke**: Menambahkan fungsionalitas untuk menghapus seluruh data di tabel `products`, `transactions`, dan `transaction_details`.
2.  **Reset Button**: Menambahkan ikon "Reset" (warna merah) di bagian kanan atas layar **Katalog Produk**.
3.  **Safety Confirmation**: Tombol reset akan memicu dialog konfirmasi untuk mencegah penghapusan data secara tidak sengaja.

## Cara Menggunakan

1.  Buka layar **Katalog Produk**.
2.  Tekan ikon **Reset** (ikon putar balik/restore merah) di pojok kanan atas bar judul.
3.  Konfirmasi dengan menekan **Hapus Semua**.
4.  Setelah riwayat transaksi bersih, Anda bisa bebas menambah dan menghapus produk tanpa batasan constraint database.

> [!WARNING]
> Menekan "Hapus Semua" akan melenyapkan seluruh data transaksi dan produk. Gunakan fitur ini hanya jika Anda ingin memulai database dari nol.

## Verifikasi
- Build gradle berhasil.
- Logic penghapusan mengikuti urutan yang benar (menghapus detail transaksi terlebih dahulu) untuk menghindari crash constraint.
