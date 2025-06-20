package com.androidprojek.unifind.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels // <-- PASTIKAN IMPORT INI ADA
import androidx.navigation.fragment.findNavController
import com.androidprojek.unifind.R
import com.androidprojek.unifind.databinding.PenemuanPostFormBinding
import com.androidprojek.unifind.viewmodel.NotificationMainViewModel // <-- PASTIKAN IMPORT INI ADA
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class penemuan_post_form : Fragment() {

    private var _binding: PenemuanPostFormBinding? = null
    private val binding get() = _binding!!

    // <-- LANGKAH 1: TAMBAHKAN BARIS INI UNTUK MENDAPATKAN VIEWMODEL
    private val sharedViewModel: NotificationMainViewModel by activityViewModels()

    private var imageUri: Uri? = null
    private val calendar = Calendar.getInstance()

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            binding.ivFotoBarang.setImageURI(it)
            binding.ivFotoBarang.visibility = View.VISIBLE
            binding.btnUnggahFoto.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PenemuanPostFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()
        setupClickListeners()
    }

    private fun setupSpinner() {
        val categories = resources.getStringArray(R.array.kategori_barang_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerKategori.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.fieldTanggal.setOnClickListener { showDatePicker() }
        binding.tvTanggalPenemuan.setOnClickListener { showDatePicker() }
        binding.fieldWaktu.setOnClickListener { showTimePicker() }
        binding.tvWaktuPenemuan.setOnClickListener { showTimePicker() }
        binding.btnUnggahFoto.setOnClickListener { pickImageLauncher.launch("image/*") }
        binding.btnBuatPostingan.setOnClickListener { validateAndPost() }
    }

    private fun showDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            binding.tvTanggalPenemuan.text = dateFormat.format(calendar.time)
        }
        DatePickerDialog(requireContext(), dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            binding.tvWaktuPenemuan.text = timeFormat.format(calendar.time) + " WIB"
        }
        TimePickerDialog(requireContext(), timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE), true).show()
    }

    private fun validateAndPost() {
        val nama = binding.edtNama.text.toString().trim()
        val nim = binding.edtNim.text.toString().trim()
        val namaBarang = binding.edtNamaBarang.text.toString().trim()
        val deskripsi = binding.edtDeskripsi.text.toString().trim()
        val tanggal = binding.tvTanggalPenemuan.text.toString().trim()
        val waktu = binding.tvWaktuPenemuan.text.toString().trim()
        val lokasi = binding.edtLokasi.text.toString().trim()

        if (nama.isEmpty() || nim.isEmpty() || namaBarang.isEmpty() || deskripsi.isEmpty() ||
            tanggal.isEmpty() || tanggal.equals("Pilih tanggal", true) ||
            waktu.isEmpty() || waktu.equals("Pilih waktu", true) ||
            lokasi.isEmpty() || imageUri == null) {
            Toast.makeText(context, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnBuatPostingan.isEnabled = false
        Toast.makeText(context, "Membuat postingan...", Toast.LENGTH_SHORT).show()

        uploadImageAndSaveData()
    }

    private fun uploadImageAndSaveData() {
        val storageRef = storage.reference.child("foto_penemuan/${UUID.randomUUID()}.jpg")
        imageUri?.let {
            storageRef.putFile(it)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        saveDataToFirestore(uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Gagal mengunggah gambar: ${e.message}", Toast.LENGTH_SHORT).show()
                    binding.btnBuatPostingan.isEnabled = true
                }
        }
    }

    private fun saveDataToFirestore(imageUrl: String) {
        val uid = auth.currentUser?.uid ?: ""

        val postData = hashMapOf(
            "uid" to uid,
            "namaPelapor" to binding.edtNama.text.toString().trim(),
            "nim" to binding.edtNim.text.toString().trim(),
            "namaBarang" to binding.edtNamaBarang.text.toString().trim(),
            "kategori" to binding.spinnerKategori.selectedItem.toString(),
            "deskripsi" to binding.edtDeskripsi.text.toString().trim(),
            "tanggalPenemuan" to binding.tvTanggalPenemuan.text.toString().trim(),
            "waktuPenemuan" to binding.tvWaktuPenemuan.text.toString().trim(),
            "lokasiPenemuan" to binding.edtLokasi.text.toString().trim(),
            "imageUrl" to imageUrl,
            "timestamp" to System.currentTimeMillis(),
            "status" to "Dalam Pencarian"
        )

        db.collection("form_penemuan")
            .add(postData)
            .addOnSuccessListener {
                // <-- LANGKAH 2: TAMBAHKAN BARIS INI SETELAH SUKSES
                sharedViewModel.addPostSuccessNotification()

                Toast.makeText(context, "Postingan berhasil dibuat!", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Gagal membuat postingan: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.btnBuatPostingan.isEnabled = true
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}