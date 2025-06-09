//package com.androidprojek.unifind.ui
//
//import android.net.Uri
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.androidprojek.unifind.R
//import com.androidprojek.unifind.databinding.ActivityDetailBarangBinding
//import com.androidprojek.unifind.model.BarangModel
//
//class DetailBarangActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityDetailBarangBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityDetailBarangBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val barang = intent.getParcelableExtra<BarangModel>("DATA_BARANG")
//        barang?.let {
//            binding.tvNama.text = "Nama: ${it.nama}"
//            binding.tvNim.text = "NIM: ${it.nim}"
//            binding.tvNamaBarang.text = "Nama Barang: ${it.namaBarang}"
//            binding.tvKategori.text = "Kategori: ${it.kategori}"
//            binding.tvDeskripsi.text = "Deskripsi: ${it.deskripsi}"
//            binding.tvTanggalWaktu.text = "Hilang: ${it.tanggalHilang} pukul ${it.waktuHilang}"
//            binding.tvLokasi.text = "Lokasi: ${it.lokasiHilang}"
//            try {
//                val uri = Uri.parse(barang.fotoUri)
//                binding.imgDetail.setImageURI(uri)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                binding.imgDetail.setImageResource(R.drawable.warning_vector)
//            }
//        }
//    }
//}
