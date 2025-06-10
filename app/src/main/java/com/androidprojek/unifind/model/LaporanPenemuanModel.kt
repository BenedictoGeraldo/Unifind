package com.androidprojek.unifind.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class LaporanPenemuanModel(
    // Info Penghubung
    val idBarangHilang: String = "",
    val uidPelaporAsli: String = "",

    // Info Si Penemu (yang mengisi form ini)
    val penemuUid: String = "",
    val penemuNama: String = "",
    val penemuNim: String = "",

    // Info Laporan Penemuan
    val deskripsiTambahan: String = "",
    val tanggalTemuan: String = "",
    val waktuTemuan: String = "",
    val lokasiTemuan: String = "",
    val fotoLaporanUris: List<String> = emptyList(), // Foto dari si penemu

    // Status
    val statusLaporan: String = "Menunggu Verifikasi",
    @ServerTimestamp val timestamp: Date? = null
) : Parcelable