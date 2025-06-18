package com.androidprojek.unifind.viewmodel

import androidx.lifecycle.ViewModel
import com.androidprojek.unifind.R
// Pastikan path ke Repository Anda benar
import com.androidprojek.unifind.datanotification.NotificationRepository
import com.androidprojek.unifind.model.NotificationModel
import java.util.Date

class NotificationMainViewModel : ViewModel() {

    // ViewModel tidak lagi menyimpan data.
    // Dia hanya menunjuk langsung ke data yang ada di Repository.
    val notificationList = NotificationRepository.notificationList

    /**
     * Fungsi ini hanya membuat model notifikasi lalu meneruskannya
     * ke Repository untuk ditambahkan.
     */
    fun addPostSuccessNotification() {
        val newNotification = NotificationModel(
            id = Date().time,
            iconResId = R.drawable.form,
            message = "Form penemuan barang kamu sudah berhasil terkirim!",
            timestamp = "baru saja"
        )
        // Meneruskan perintah ke Repository
        NotificationRepository.addNotification(newNotification)
    }

    /**
     * Fungsi ini juga hanya membuat model notifikasi lalu meneruskannya
     * ke Repository untuk ditambahkan.
     */
    fun addSearchSuccessNotification() {
        val newNotification = NotificationModel(
            id = Date().time,
            iconResId = R.drawable.form,
            message = "form pencarian barang kamu sudah berhasil terkirim.",
            timestamp = "baru saja"
        )
        // Meneruskan perintah ke Repository
        NotificationRepository.addNotification(newNotification)
    }
}