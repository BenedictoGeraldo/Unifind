package com.androidprojek.unifind.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PenemuanModel(
    // Properti ini harus sama persis dengan yang ada di `penemuan_post_form`
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