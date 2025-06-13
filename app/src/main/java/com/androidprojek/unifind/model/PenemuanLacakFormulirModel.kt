package com.androidprojek.unifind.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model data ini khusus untuk halaman "Lacak Formulir".
 * Ia menggabungkan informasi dari postingan asli dan klaim yang dikirim.
 */
@Parcelize
data class PenemuanLacakFormulirModel(
    // Info dari postingan asli
    val postId: String? = null,
    val namaBarangPostingan: String? = null,
    val imageUrlPostingan: String? = null,
    val namaPenemu: String? = null,

    // Info dari klaim yang dikirim
    val klaimId: String? = null,
    val statusKlaim: String? = null,

    // Seluruh data dari formulir klaim, untuk dikirim jika "Lihat Jawaban" diklik
    val detailKlaim: PenemuanKlaimModel? = null
) : Parcelable
