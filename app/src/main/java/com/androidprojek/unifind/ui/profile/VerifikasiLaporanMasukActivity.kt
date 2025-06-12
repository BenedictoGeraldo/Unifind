package com.androidprojek.unifind.ui.profile

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        bindDataToViews()
        setupActionButtons()
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener { finish() }
    }

    private fun bindDataToViews() {
        laporan?.let {
            binding.tvDetailWaktuTemuan.text = "Ditemukan pada ${it.tanggalTemuan}, pukul ${it.waktuTemuan}"
            binding.tvDetailLokasiTemuan.text = "Di ${it.lokasiTemuan}"
            binding.tvDetailDeskripsiPenemu.text = it.deskripsiTambahan.ifEmpty { "Tidak ada deskripsi tambahan." }

            if (it.fotoLaporanUris.isNotEmpty()) {
                binding.viewPagerBukti.adapter = ImageSliderAdapter(it.fotoLaporanUris)
                binding.dotsIndicatorBukti.attachTo(binding.viewPagerBukti)
            } else {
                binding.viewPagerBukti.visibility = View.GONE
                binding.dotsIndicatorBukti.visibility = View.GONE
            }
        }
    }

    private fun setupActionButtons() {
        binding.btnTolak.setOnClickListener {
            // Memanggil fungsi yang sama, hanya dengan status "Ditolak"
            updateStatusLaporan("Ditolak")
        }
        binding.btnSetujui.setOnClickListener {
            updateStatusLaporan("Disetujui")
        }
    }

    // --- FUNGSI INI DIMODIFIKASI SECARA TOTAL ---
    private fun updateStatusLaporan(newStatus: String) {
        setLoading(true) // Tampilkan loading saat proses dimulai

        // Validasi ID
        if (laporan?.id.isNullOrEmpty() || laporan?.idBarangHilang.isNullOrEmpty()) {
            Toast.makeText(this, "ID Laporan atau Barang tidak valid.", Toast.LENGTH_SHORT).show()
            setLoading(false)
            return
        }

        // 1. Buat instance Batched Write
        val batch = db.batch()

        // 2. Siapkan referensi ke dokumen Laporan
        val laporanRef = db.collection("barangHilang").document(laporan!!.idBarangHilang)
            .collection("laporanPenemuan").document(laporan!!.id!!)

        // 3. Tambahkan operasi update status laporan ke dalam batch
        batch.update(laporanRef, "statusLaporan", newStatus)

        // 4. JIKA laporan disetujui, tambahkan juga operasi update status barang utama
        if (newStatus == "Disetujui") {
            // Siapkan referensi ke dokumen Barang Hilang utama
            val barangRef = db.collection("barangHilang").document(laporan!!.idBarangHilang)
            // Tambahkan operasi update status postingan ke dalam batch
            batch.update(barangRef, "status", "Ditemukan")
        }

        // 5. Jalankan semua operasi di dalam batch
        batch.commit()
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, "Proses berhasil!", Toast.LENGTH_LONG).show()
                finish() // Kembali ke halaman sebelumnya
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Gagal mengupdate status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Fungsi helper untuk menampilkan/menyembunyikan loading
    private fun setLoading(isLoading: Boolean) {
        // Anda mungkin perlu menambahkan ProgressBar di layout XML Anda
        // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSetujui.isEnabled = !isLoading
        binding.btnTolak.isEnabled = !isLoading
    }
}