package com.androidprojek.unifind.model

data class Tracking(
    val id: String = "",
    val namaBarang: String = "",
    val kategoriBarang: String = "",
    val deskripsiBarang: String = "",
    val idPerangkat: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = 0L
)