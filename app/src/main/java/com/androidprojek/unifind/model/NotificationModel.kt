package com.androidprojek.unifind.model

import androidx.annotation.DrawableRes

/**
 * Data class ini merepresentasikan satu buah item di dalam daftar notifikasi.
 */
data class NotificationModel(
    val id: Long, // ID unik untuk setiap notifikasi, bisa dari timestamp atau database

    @DrawableRes
    val iconResId: Int, // Resource ID untuk gambar ikon (contoh: R.drawable.form)

    val message: String, // Teks utama notifikasi (contoh: "Form Verifikasi Penemu...")

    val timestamp: String, // Teks waktu (contoh: "baru saja", "1 menit yang lalu")

    val isRead: Boolean = false // Status untuk menandai notifikasi sudah dibaca atau belum
)