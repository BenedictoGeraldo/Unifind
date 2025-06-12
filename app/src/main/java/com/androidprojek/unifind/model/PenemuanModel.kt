package com.androidprojek.unifind.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class PenemuanModel(
    // TAMBAHKAN PROPERTI INI UNTUK MENYIMPAN ID DOKUMEN DARI FIRESTORE
    @get:Exclude var id: String? = null,

    // Properti lain yang sudah ada
    val namaPelapor: String? = null,
    val nim: String? = null,
    val namaBarang: String? = null,
    val kategori: String? = null,
    val deskripsi: String? = null,
    val tanggalPenemuan: String? = null,
    val waktuPenemuan: String? = null,
    val lokasiPenemuan: String? = null,
    val imageUrl: String? = null,
    val timestamp: Long? = 0L,
    val uid: String? = null
) : Parcelable
