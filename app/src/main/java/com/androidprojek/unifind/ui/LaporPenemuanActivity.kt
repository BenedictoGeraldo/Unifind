package com.androidprojek.unifind.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.androidprojek.unifind.databinding.ActivityLaporPenemuanBinding
import com.androidprojek.unifind.model.LaporanPenemuanModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class LaporPenemuanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaporPenemuanBinding

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    // Data dari Intent
    private var idBarangAsli: String? = null
    private var uidPelaporAsli: String? = null

    // Data Penemu (pengguna saat ini)
    private var penemuNama: String = ""
    private var penemuNim: String = ""
    // Tambahkan variabel untuk menampung kontak penemu
    private var penemuInstagram: String? = null
    private var penemuLine: String? = null
    private var penemuWhatsapp: String? = null

    // Data Form
    private val selectedImageUris = mutableListOf<Uri>()
    private val calendar = Calendar.getInstance()

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            handleImageSelection(result.data)
        }
    }

    companion object {
        const val EXTRA_BARANG_ID = "extra_barang_id"
        const val EXTRA_NAMA_BARANG = "extra_nama_barang"
        const val EXTRA_KATEGORI = "extra_kategori"
        const val EXTRA_PELAPOR_UID = "extra_pelapor_uid"
        const val EXTRA_NAMA_PELAPOR = "extra_nama_pelapor"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaporPenemuanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Setup UI dan listener
        setupToolbar()
        getIntentData()
        setupDateTimePickers()
        setupClickListeners()

        // --- PERUBAHAN LOGIKA UTAMA ---
        // 1. Nonaktifkan tombol kirim di awal dan tampilkan loading profil
        setSubmitButtonEnabled(false)
        binding.progressBar.visibility = View.VISIBLE

        // 2. Ambil profil pengguna
        fetchFinderProfile()
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun getIntentData() {
        idBarangAsli = intent.getStringExtra(EXTRA_BARANG_ID)
        uidPelaporAsli = intent.getStringExtra(EXTRA_PELAPOR_UID)
        val namaBarang = intent.getStringExtra(EXTRA_NAMA_BARANG)
        val kategori = intent.getStringExtra(EXTRA_KATEGORI)
        val namaPelapor = intent.getStringExtra(EXTRA_NAMA_PELAPOR)

        binding.tvNamaBarangAsli.text = "Nama Barang: $namaBarang"
        binding.tvKategoriAsli.text = "Kategori: $kategori"
        binding.tvNamaPelaporAsli.text = "Pelapor: $namaPelapor"
    }

    // --- FUNGSI INI SEKARANG MENGONTROL UI ---
    private fun fetchFinderProfile() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Sesi tidak valid, silakan login ulang.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Tampilkan loading saat proses dimulai
        binding.progressBar.visibility = View.VISIBLE
        setSubmitButtonEnabled(false) // Nonaktifkan tombol selama loading

        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                binding.progressBar.visibility = View.GONE // Sembunyikan loading
                if (document.exists()) {
                    // --- PERBAIKAN DI SINI ---
                    // Gunakan Elvis Operator (?:) untuk memberikan nilai default jika data dari Firestore null
                    penemuNama = document.getString("nama") ?: "Tanpa Nama"
                    penemuNim = document.getString("nim") ?: "Tanpa NIM"

                    // Untuk kontak, karena variabelnya sudah nullable (String?), kita tidak perlu Elvis Operator
                    penemuInstagram = document.getString("instagram")
                    penemuLine = document.getString("line")
                    penemuWhatsapp = document.getString("whatsapp")

                    // JIKA SUKSES, AKTIFKAN KEMBALI TOMBOL KIRIM
                    setSubmitButtonEnabled(true)
                    Log.d("ProfileFetch", "Profil berhasil dimuat: $penemuNama")
                } else {
                    Toast.makeText(this, "Data profil tidak ditemukan.", Toast.LENGTH_SHORT).show()
                    // Biarkan tombol nonaktif jika profil tidak ditemukan
                }
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE // Sembunyikan loading
                Toast.makeText(this, "Gagal memuat profil: ${e.message}", Toast.LENGTH_SHORT).show()
                // Biarkan tombol nonaktif jika gagal
            }
    }

    private fun setupClickListeners() {
        binding.btnUploadGambar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            imagePickerLauncher.launch(intent)
        }

        binding.btnKirimLaporan.setOnClickListener {
            if (isValidInput()) {
                uploadImagesAndSaveReport()
            }
        }
    }

    private fun setupDateTimePickers() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year); calendar.set(Calendar.MONTH, month); calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            binding.tvTanggalTemuan.text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
        }
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay); calendar.set(Calendar.MINUTE, minute)
            binding.tvWaktuTemuan.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        }
        binding.tvTanggalTemuan.setOnClickListener {
            DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        binding.tvWaktuTemuan.setOnClickListener {
            TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }

    private fun handleImageSelection(data: Intent?) {
        if (data == null) return
        selectedImageUris.clear()
        if (data.clipData != null) {
            for (i in 0 until data.clipData!!.itemCount) {
                selectedImageUris.add(data.clipData!!.getItemAt(i).uri)
            }
        } else if (data.data != null) {
            selectedImageUris.add(data.data!!)
        }
        binding.tvImageCount.text = "${selectedImageUris.size} gambar dipilih"
        binding.tvImageCount.visibility = View.VISIBLE
    }

    private fun isValidInput(): Boolean {
        if (binding.tvTanggalTemuan.text.isEmpty() || binding.tvWaktuTemuan.text.isEmpty() || binding.etLokasiTemuan.text.isBlank()) {
            Toast.makeText(this, "Tanggal, Waktu, dan Lokasi ditemukan wajib diisi.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Foto barang bukti wajib diunggah.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun uploadImagesAndSaveReport() {
        setLoading(true)
        val uploadedImageUrls = mutableListOf<String>()
        var uploadCounter = 0

        for (uri in selectedImageUris) {
            val fileName = "laporan_penemuan_images/${System.currentTimeMillis()}_${uri.lastPathSegment}"
            val storageRef = storage.reference.child(fileName)
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        uploadedImageUrls.add(downloadUrl.toString())
                        uploadCounter++
                        if (uploadCounter == selectedImageUris.size) {
                            saveReportToFirestore(uploadedImageUrls)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    setLoading(false)
                    Toast.makeText(this, "Gagal mengunggah gambar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun saveReportToFirestore(imageUrls: List<String>) {
        val penemu = auth.currentUser
        // Lakukan validasi yang lebih ketat di sini
        if (penemu == null || idBarangAsli == null || uidPelaporAsli == null || penemuNama == null) {
            setLoading(false)
            Toast.makeText(this, "Gagal menyimpan, data pengguna tidak lengkap.", Toast.LENGTH_SHORT).show()
            return
        }

        val laporan = LaporanPenemuanModel(
            idBarangHilang = idBarangAsli!!,
            uidPelaporAsli = uidPelaporAsli!!,
            penemuUid = penemu.uid,
            penemuNama = this.penemuNama!!,
            penemuNim = this.penemuNim ?: "", // NIM boleh kosong
            penemuInstagram = this.penemuInstagram,
            penemuLine = this.penemuLine,
            penemuWhatsapp = this.penemuWhatsapp,
            deskripsiTambahan = binding.etDeskripsiTambahan.text.toString().trim(),
            tanggalTemuan = binding.tvTanggalTemuan.text.toString(),
            waktuTemuan = binding.tvWaktuTemuan.text.toString(),
            lokasiTemuan = binding.etLokasiTemuan.text.toString().trim(),
            fotoLaporanUris = imageUrls,
            statusLaporan = "Menunggu Verifikasi"
        )

        db.collection("barangHilang").document(idBarangAsli!!)
            .collection("laporanPenemuan")
            .add(laporan)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, "Laporan penemuan berhasil dikirim!", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Gagal mengirim laporan: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        // Sekarang setLoading hanya mengatur ProgressBar untuk proses upload gambar
    }

    // --- TAMBAHKAN FUNGSI BARU INI ---
    private fun setSubmitButtonEnabled(isEnabled: Boolean) {
        binding.btnKirimLaporan.isEnabled = isEnabled
        // Beri efek visual agar terlihat nonaktif
        binding.btnKirimLaporan.alpha = if (isEnabled) 1.0f else 0.5f
    }
}