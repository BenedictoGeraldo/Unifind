package com.androidprojek.unifind.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class BarangModel(
    @get:Exclude var id: String? = null,

    // --- Data Pelapor ---
    val pelaporUid: String = "",
    val nama: String = "",
    val nim: String = "",
    val pelaporPhotoUrl: String = "",   // <-- FOTO PROFIL PELAPOR
    val pelaporInstagram: String = "",  // <-- KONTAK INSTAGRAM PELAPOR
    val pelaporLine: String = "",       // <-- KONTAK LINE PELAPOR
    val pelaporWhatsapp: String = "",   // <-- KONTAK WHATSAPP PELAPOR

    // --- Data Barang ---
    val namaBarang: String = "",
    val kategori: String = "",
    val deskripsi: String = "",
    val tanggalHilang: String = "",
    val waktuHilang: String = "",
    val lokasiHilang: String = "",
    val fotoUris: List<String> = emptyList(),
    val status: String = "Dalam Pencarian",

    @ServerTimestamp val timestamp: Date? = null
) : Parcelable