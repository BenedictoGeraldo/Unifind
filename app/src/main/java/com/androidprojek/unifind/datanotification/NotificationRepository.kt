package com.androidprojek.unifind.datanotification

import androidx.lifecycle.MutableLiveData
import com.androidprojek.unifind.R
import com.androidprojek.unifind.model.NotificationModel
import java.util.Date

/**
 * Ini adalah Singleton Repository, satu-satunya sumber data notifikasi
 * untuk seluruh aplikasi.
 * 'object' memastikan hanya ada satu instance dari kelas ini.
 */
object NotificationRepository {
    val notificationList = MutableLiveData<MutableList<NotificationModel>>().apply {
        // Sekarang listnya dimulai dalam keadaan benar-benar kosong.
        value = mutableListOf()
    }

    /**
     * Fungsi terpusat untuk menambah notifikasi baru ke dalam daftar.
     * Fungsi ini bisa dipanggil dari mana saja di dalam aplikasi.
     * @param notification Objek NotificationModel yang akan ditambahkan.
     */
    fun addNotification(notification: NotificationModel) {
        // Ambil daftar yang ada saat ini, atau buat list baru jika null
        val currentList = notificationList.value ?: mutableListOf()

        // Tambahkan item baru ke posisi paling atas (indeks 0)
        currentList.add(0, notification)

        // Set nilai baru ke LiveData agar semua observer mendeteksi perubahan
        notificationList.value = currentList
    }
}