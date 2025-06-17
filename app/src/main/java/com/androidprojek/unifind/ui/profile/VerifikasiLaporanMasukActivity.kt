package com.androidprojek.unifind.ui.profile

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidprojek.unifind.R
import com.androidprojek.unifind.adapter.ImageSliderAdapter
import com.androidprojek.unifind.databinding.ActivityVerifikasiLaporanMasukBinding
import com.androidprojek.unifind.model.LaporanPenemuanModel
import com.google.firebase.firestore.FirebaseFirestore

class VerifikasiLaporanMasukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifikasiLaporanMasukBinding
    private var laporan: LaporanPenemuanModel? = null
    private val db = FirebaseFirestore.getInstance()

    companion object {
        const val EXTRA_LAPORAN = "extra_laporan"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifikasiLaporanMasukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data laporan LENGKAP dari intent (karena sudah dibuat Parcelable)
        laporan = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_LAPORAN, LaporanPenemuanModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_LAPORAN)
        }

        if (laporan == null) {
            Toast.makeText(this, "Gagal memuat data laporan.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        // Cukup panggil satu fungsi untuk menampilkan semua data yang sudah kita miliki
        bindDataToViews(laporan!!)
        setupActionButtons()
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener { finish() }
    }

    // --- FUNGSI INI SEKARANG MENAMPILKAN SEMUA DATA DARI OBJEK 'laporan' ---
    private fun bindDataToViews(laporan: LaporanPenemuanModel) {
        binding.apply {
            // Mengisi data penemu
            tvVerifikasiNamaPenemu.text = laporan.penemuNama
            tvVerifikasiNimPenemu.text = laporan.penemuNim

            // Mengisi data detail laporan penemuan
            tvVerifikasiTanggal.text = laporan.tanggalTemuan
            tvVerifikasiWaktu.text = laporan.waktuTemuan
            tvVerifikasiLokasi.text = laporan.lokasiTemuan
            tvVerifikasiDeskripsi.text = laporan.deskripsiTambahan.ifEmpty { "Tidak ada deskripsi tambahan." }

            // Field-field ini tidak memiliki data di LaporanPenemuanModel, jadi kita sembunyikan saja
            // atau Anda bisa hapus dari file XML jika mau.
            tvVerifikasiNamaBarang.visibility = View.GONE
            findViewById<View>(R.id.label_nama_barang).visibility = View.GONE // Asumsikan Anda memberi ID pada labelnya
            tvVerifikasiKategori.visibility = View.GONE
            findViewById<View>(R.id.label_kategori).visibility = View.GONE // Asumsikan Anda memberi ID pada labelnya

            // Logika untuk menampilkan foto bukti atau teks "kosong"
            if (laporan.fotoLaporanUris.isNotEmpty()) {
                viewPagerBukti.visibility = View.VISIBLE
                dotsIndicatorBukti.visibility = View.VISIBLE
                tvFotoBuktiKosong.visibility = View.GONE

                viewPagerBukti.adapter = ImageSliderAdapter(laporan.fotoLaporanUris)
                dotsIndicatorBukti.attachTo(viewPagerBukti)
            } else {
                viewPagerBukti.visibility = View.GONE
                dotsIndicatorBukti.visibility = View.GONE
                tvFotoBuktiKosong.visibility = View.VISIBLE
            }
        }
    }

    private fun setupActionButtons() {
        binding.btnTolak.setOnClickListener {
            updateStatusLaporan("Ditolak")
        }
        binding.btnSetujui.setOnClickListener {
            updateStatusLaporan("Disetujui")
        }
    }

    private fun updateStatusLaporan(newStatus: String) {
        if (laporan?.id.isNullOrEmpty() || laporan?.idBarangHilang.isNullOrEmpty()) {
            Toast.makeText(this, "ID Laporan atau Barang tidak valid.", Toast.LENGTH_SHORT).show()
            return
        }

        val reportRef = db.collection("barangHilang").document(laporan!!.idBarangHilang)
            .collection("laporanPenemuan").document(laporan!!.id!!)

        val batch = db.batch()
        batch.update(reportRef, "statusLaporan", newStatus)

        if (newStatus == "Disetujui") {
            val barangRef = db.collection("barangHilang").document(laporan!!.idBarangHilang)
            batch.update(barangRef, "status", "Ditemukan")
        }

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(this, "Laporan berhasil diubah menjadi '$newStatus'", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengupdate status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}