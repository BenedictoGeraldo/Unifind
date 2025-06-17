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
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    // Data dari postingan asli yang diterima via Intent
    private var idBarangAsli: String? = null
    private var uidPelaporAsli: String? = null
    private var namaBarangAsli: String? = null
    private var kategoriAsli: String? = null

    // Data Penemu (pengguna saat ini)
    private var penemuNama: String? = null
    private var penemuNim: String? = null
    private var penemuInstagram: String? = null
    private var penemuLine: String? = null
    private var penemuWhatsapp: String? = null
    private var penemuPhotoUrl: String? = null

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

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        getIntentData()
        setupListeners()

        // Nonaktifkan tombol kirim di awal & mulai ambil data profil
        setSubmitButtonEnabled(false)
        fetchFinderProfile()
    }

    private fun getIntentData() {
        idBarangAsli = intent.getStringExtra(EXTRA_BARANG_ID)
        uidPelaporAsli = intent.getStringExtra(EXTRA_PELAPOR_UID)
        // Simpan nama barang dan kategori ke variabel
        namaBarangAsli = intent.getStringExtra(EXTRA_NAMA_BARANG)
        kategoriAsli = intent.getStringExtra(EXTRA_KATEGORI)
    }

    private fun fetchFinderProfile() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Sesi tidak valid, silakan login ulang.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Anda bisa menambahkan ProgressBar di XML jika ingin ada indikator loading visual
        // binding.progressBar.visibility = View.VISIBLE

        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                // binding.progressBar.visibility = View.GONE
                if (document.exists()) {
                    // Ambil semua data profil penemu
                    penemuNama = document.getString("nama")
                    penemuNim = document.getString("nim")
                    penemuInstagram = document.getString("instagram")
                    penemuLine = document.getString("line")
                    penemuWhatsapp = document.getString("whatsapp")
                    penemuPhotoUrl = document.getString("photoUrl")

                    // Langsung isikan ke TextView yang sekarang berfungsi sebagai field read-only
                    binding.tvPenemuNama.text = penemuNama ?: "Data tidak ditemukan"
                    binding.tvPenemuNim.text = penemuNim ?: "Data tidak ditemukan"

                    // Aktifkan tombol kirim setelah data profil berhasil dimuat
                    setSubmitButtonEnabled(true)
                } else {
                    Toast.makeText(this, "Data profil tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Gagal memuat profil: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupListeners() {
        binding.toolbarLaporPenemuan.setOnClickListener { finish() }

        binding.btnUnggahFoto.setOnClickListener {
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

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year); calendar.set(Calendar.MONTH, month); calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            binding.tvTanggalTemuan.text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
        }
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay); calendar.set(Calendar.MINUTE, minute)
            binding.tvWaktuTemuan.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        }
        // Pastikan ID untuk field tanggal dan waktu sesuai dengan layout XML yang baru
        binding.fieldTanggalTemuan.setOnClickListener {
            DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        binding.fieldWaktuTemuan.setOnClickListener {
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
        // Di sini Anda bisa menampilkan preview gambar di RecyclerView jika mau
        // binding.rvFotoBukti.visibility = View.VISIBLE
        Toast.makeText(this, "${selectedImageUris.size} gambar dipilih", Toast.LENGTH_SHORT).show()
    }

    private fun isValidInput(): Boolean {
        if (binding.tvTanggalTemuan.text.isEmpty() || binding.tvWaktuTemuan.text.isEmpty() || binding.edtLokasiTemuan.text.isBlank()) {
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
        // Anda bisa menampilkan loading di sini jika mau
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
                    Toast.makeText(this, "Gagal mengunggah gambar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun saveReportToFirestore(imageUrls: List<String>) {
        val penemu = auth.currentUser
        if (penemu == null || idBarangAsli == null || uidPelaporAsli == null || penemuNama == null) {
            Toast.makeText(this, "Gagal menyimpan, data pengguna tidak lengkap.", Toast.LENGTH_SHORT).show()
            return
        }

        val laporan = LaporanPenemuanModel(
            idBarangHilang = idBarangAsli!!,
            uidPelaporAsli = uidPelaporAsli!!,
            penemuUid = penemu.uid,
            penemuNama = this.penemuNama!!,
            penemuNim = this.penemuNim ?: "",
            penemuInstagram = this.penemuInstagram,
            penemuLine = this.penemuLine,
            penemuWhatsapp = this.penemuWhatsapp,
            penemuPhotoUrl = this.penemuPhotoUrl,
            // Menyimpan nama barang dan kategori dari postingan asli
            namaBarangPostingan = this.namaBarangAsli,
            kategoriPostingan = this.kategoriAsli,
            deskripsiTambahan = binding.edtDeskripsiTambahan.text.toString().trim(),
            tanggalTemuan = binding.tvTanggalTemuan.text.toString(),
            waktuTemuan = binding.tvWaktuTemuan.text.toString(),
            lokasiTemuan = binding.edtLokasiTemuan.text.toString().trim(),
            fotoLaporanUris = imageUrls,
            statusLaporan = "Menunggu Verifikasi"
        )

        db.collection("barangHilang").document(idBarangAsli!!)
            .collection("laporanPenemuan")
            .add(laporan)
            .addOnSuccessListener {
                Toast.makeText(this, "Laporan penemuan berhasil dikirim!", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengirim laporan: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setSubmitButtonEnabled(isEnabled: Boolean) {
        binding.btnKirimLaporan.isEnabled = isEnabled
        binding.btnKirimLaporan.alpha = if (isEnabled) 1.0f else 0.5f
    }
}