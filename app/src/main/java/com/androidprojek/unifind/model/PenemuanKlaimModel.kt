package com.androidprojek.unifind.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

// --- 1. TAMBAHKAN ANOTASI @Parcelize ---
@Parcelize
data class PenemuanKlaimModel(
    @get:Exclude var id: String? = null,
    val uidPengklaim: String? = null,
    val namaPengklaim: String? = null,
    val nimPengklaim: String? = null,
    val namaBarangKlaim: String? = null,
    val kategoriKlaim: String? = null,
    val deskripsiKlaim: String? = null,
    val tanggalHilangKlaim: String? = null,
    val waktuHilangKlaim: String? = null,
    val lokasiHilangKlaim: String? = null,
    val imageUrlKlaim: String? = null,
    val timestampKlaim: Long? = 0L,
    val statusKlaim: String? = null
// --- 2. IMPLEMENTASIKAN Parcelable ---
) : Parcelable
