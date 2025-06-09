package com.androidprojek.unifind.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidprojek.unifind.R
import com.androidprojek.unifind.databinding.ActivityFormBarangBinding
import com.androidprojek.unifind.model.BarangModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class FormBarangActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBarangBinding
    // --- PERUBAHAN 1: Menggunakan List untuk menampung banyak URI gambar ---
    private val selectedImageUris = mutableListOf<Uri>()
    private val calendar = Calendar.getInstance()

    // Inisialisasi Firebase
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    companion object {
        const val PICK_IMAGES_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hubungkan ke instance Firebase
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        setupSpinner()
        setupDateTimePickers()
        setupButtonListeners()
    }

    // Fungsi setupSpinner dan setupDateTimePickers tidak berubah, biarkan seperti apa adanya...
    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            this, R.array.kategori_barang, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerKategori.adapter = adapter
        }
    }
    private fun setupDateTimePickers() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year); calendar.set(Calendar.MONTH, month); calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay); calendar.set(Calendar.MINUTE, minute)
            updateTimeInView()
        }
        binding.tvTanggal.setOnClickListener {
            DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        binding.tvWaktu.setOnClickListener {
            TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }


    private fun setupButtonListeners() {
        binding.btnUploadGambar.setOnClickListener {
            // --- PERUBAHAN 2: Intent untuk memilih BANYAK gambar ---
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, PICK_IMAGES_REQUEST)
        }

        binding.btnSimpan.setOnClickListener {
            if (isValidInput()) {
                // Memulai proses upload dan simpan ke Firebase
                uploadImagesAndSaveData()
            }
        }
    }

    // --- PERUBAHAN 3: Logika onActivityResult untuk menangani banyak gambar ---
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUris.clear() // Kosongkan list sebelum diisi lagi
            if (data.clipData != null) {
                // Pengguna memilih lebih dari satu gambar
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    selectedImageUris.add(imageUri)
                }
            } else if (data.data != null) {
                // Pengguna hanya memilih satu gambar
                val imageUri = data.data!!
                selectedImageUris.add(imageUri)
            }

            // Update UI untuk menunjukkan jumlah gambar yang dipilih
            binding.tvImageCount.text = "${selectedImageUris.size} gambar dipilih"
            binding.tvImageCount.visibility = View.VISIBLE
        }
    }

    private fun isValidInput(): Boolean {
        // (Validasi lain tetap sama...)
        if (binding.etNama.text.isBlank() || binding.etNamaBarang.text.isBlank()) {
            Toast.makeText(this, "Nama dan Nama Barang tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.spinnerKategori.selectedItemPosition == 0) {
            Toast.makeText(this, "Silakan pilih kategori barang", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.tvTanggal.text.isBlank() || binding.tvWaktu.text.isBlank()) {
            Toast.makeText(this, "Silakan tentukan tanggal dan waktu", Toast.LENGTH_SHORT).show()
            return false
        }
        // --- PERUBAHAN 4: Cek jika list gambar tidak kosong ---
        if (selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Silakan pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // --- PERUBAHAN 5: Fungsi inti untuk upload banyak gambar dan simpan ke Firestore ---
    private fun uploadImagesAndSaveData() {
        setLoading(true)

        val uploadedImageUrls = mutableListOf<String>()
        var uploadCounter = 0

        if (selectedImageUris.isEmpty()) {
            setLoading(false)
            return
        }

        // Loop untuk mengunggah setiap gambar
        for (uri in selectedImageUris) {
            val fileName = "images/${System.currentTimeMillis()}_${uri.lastPathSegment}"
            val storageRef = storage.reference.child(fileName)

            storageRef.putFile(uri)
                .addOnSuccessListener {
                    // Jika satu gambar berhasil di-upload, dapatkan URL-nya
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        uploadedImageUrls.add(downloadUrl.toString())
                        uploadCounter++

                        // Cek apakah semua gambar sudah ter-upload
                        if (uploadCounter == selectedImageUris.size) {
                            // Jika ya, simpan semua data ke Firestore
                            saveDataToFirestore(uploadedImageUrls)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Jika ada satu saja yang gagal, hentikan proses
                    setLoading(false)
                    Toast.makeText(this, "Gagal mengunggah gambar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun saveDataToFirestore(imageUrls: List<String>) {
        val barang = BarangModel(
            nama = binding.etNama.text.toString(),
            nim = binding.etNim.text.toString(),
            namaBarang = binding.etNamaBarang.text.toString(),
            kategori = binding.spinnerKategori.selectedItem.toString(),
            deskripsi = binding.etDeskripsi.text.toString(),
            tanggalHilang = binding.tvTanggal.text.toString(),
            waktuHilang = binding.tvWaktu.text.toString(),
            lokasiHilang = binding.etLokasi.text.toString(),
            fotoUris = imageUrls, // Simpan list URL, bukan satu string lagi
            status = "Dalam Pencarian"
        )

        db.collection("barangHilang")
            .add(barang)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, "Laporan berhasil dibuat!", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSimpan.isEnabled = !isLoading
        binding.btnUploadGambar.isEnabled = !isLoading
    }

    // Fungsi updateDateInView dan updateTimeInView tidak berubah...
    private fun updateDateInView() {
        val myFormat = "dd-MM-yyyy"; val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.tvTanggal.text = sdf.format(calendar.time)
    }
    private fun updateTimeInView() {
        val myFormat = "HH:mm"; val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.tvWaktu.text = sdf.format(calendar.time)
    }
}