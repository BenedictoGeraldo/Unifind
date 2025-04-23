package com.androidprojek.unifind.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BarangModel(
    val nama: String,
    val nim: String,
    val namaBarang: String,
    val kategori: String,
    val deskripsi: String,
    val tanggalHilang: String,
    val waktuHilang: String,
    val lokasiHilang: String,
    val fotoUri: String
) : Parcelable
