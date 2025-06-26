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
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider // <-- PASTIKAN IMPORT INI ADA
import com.androidprojek.unifind.R
import com.androidprojek.unifind.databinding.ActivityFormBarangBinding
import com.androidprojek.unifind.model.BarangModel
import com.androidprojek.unifind.model.UserModel
import com.androidprojek.unifind.viewmodel.NotificationMainViewModel // <-- PASTIKAN IMPORT INI ADA
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class FormBarangActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBarangBinding
    private val selectedImageUris = mutableListOf<Uri>()
    private val calendar = Calendar.getInstance()

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    // <-- PERUBAHAN 1: Deklarasikan ViewModel
    private lateinit var sharedViewModel: NotificationMainViewModel

    private var currentUserProfile: UserModel? = null

    companion object {
        const val PICK_IMAGES_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnBack: ImageView = findViewById(R.id.btnBack)

        // 2. Set OnClickListener untuk ImageView
        btnBack.setOnClickListener {
            // 3. Panggil finish() saat di-klik untuk menutup Activity ini dan kembali
            finish()
        }

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        // <-- PERUBAHAN 2: Inisialisasi ViewModel
        sharedViewModel = ViewModelProvider(this).get(NotificationMainViewModel::class.java)

        fetchUserProfile()
        setupSpinner()
        setupDateTimePickers()
        setupButtonListeners()
    }

    private fun fetchUserProfile() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Sesi login tidak valid.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setLoading(true)
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    currentUserProfile = document.toObject(UserModel::class.java)
                } else {
                    Toast.makeText(this, "Data profil tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
                setLoading(false)
            }
            .addOnFailureListener {
                setLoading(false)
                Toast.makeText(this, "Gagal mengambil data profil.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveDataToFirestore(imageUrls: List<String>) {
        val currentUser = auth.currentUser
        if (currentUser == null || currentUserProfile == null) {
            setLoading(false)
            Toast.makeText(this, "Tidak bisa menyimpan, data pengguna tidak lengkap.", Toast.LENGTH_SHORT).show()
            return
        }

        val barang = BarangModel(
            pelaporUid = currentUser.uid,
            nama = currentUserProfile!!.nama,
            nim = currentUserProfile!!.nim,
            pelaporPhotoUrl = currentUserProfile!!.photoUrl,
            pelaporInstagram = currentUserProfile!!.instagram,
            pelaporLine = currentUserProfile!!.line,
            pelaporWhatsapp = currentUserProfile!!.whatsapp,
            namaBarang = binding.etNamaBarang.text.toString(),
            kategori = binding.spinnerKategori.selectedItem.toString(),
            deskripsi = binding.etDeskripsi.text.toString(),
            tanggalHilang = binding.tvTanggal.text.toString(),
            waktuHilang = binding.tvWaktu.text.toString(),
            lokasiHilang = binding.etLokasi.text.toString(),
            fotoUris = imageUrls,
            status = "Dalam Pencarian"
        )

        db.collection("barangHilang").add(barang)
            .addOnSuccessListener {
                // <-- PERUBAHAN 3: Panggil ViewModel untuk membuat notifikasi
                sharedViewModel.addSearchSuccessNotification()

                setLoading(false)
                Toast.makeText(this, "Laporan berhasil dibuat!", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // ... (Sisa fungsi lainnya tidak perlu diubah) ...
    private fun setupSpinner() {
        ArrayAdapter.createFromResource(this, R.array.kategori_barang, android.R.layout.simple_spinner_item).also { adapter ->
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
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, PICK_IMAGES_REQUEST)
        }
        binding.btnSimpan.setOnClickListener {
            if (isValidInput()) {
                uploadImagesAndSaveData()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
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
    }

    private fun isValidInput(): Boolean {
        if (binding.etNamaBarang.text.isBlank()) {
            Toast.makeText(this, "Nama Barang tidak boleh kosong", Toast.LENGTH_SHORT).show()
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
        if (selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Silakan pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun uploadImagesAndSaveData() {
        setLoading(true)
        val uploadedImageUrls = mutableListOf<String>()
        var uploadCounter = 0
        if (selectedImageUris.isEmpty()) {
            setLoading(false)
            return
        }
        for (uri in selectedImageUris) {
            val fileName = "images/${System.currentTimeMillis()}_${uri.lastPathSegment}"
            val storageRef = storage.reference.child(fileName)
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        uploadedImageUrls.add(downloadUrl.toString())
                        uploadCounter++
                        if (uploadCounter == selectedImageUris.size) {
                            saveDataToFirestore(uploadedImageUrls)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    setLoading(false)
                    Toast.makeText(this, "Gagal mengunggah gambar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSimpan.isEnabled = !isLoading
        binding.btnUploadGambar.isEnabled = !isLoading
    }

    private fun updateDateInView() {
        val myFormat = "dd-MM-yyyy"; val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.tvTanggal.text = sdf.format(calendar.time)
    }

    private fun updateTimeInView() {
        val myFormat = "HH:mm"; val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.tvWaktu.text = sdf.format(calendar.time)
    }
}