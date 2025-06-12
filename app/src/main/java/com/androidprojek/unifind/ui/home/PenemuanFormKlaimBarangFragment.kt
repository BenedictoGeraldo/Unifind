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
import androidx.navigation.fragment.findNavController
import com.androidprojek.unifind.R
import com.androidprojek.unifind.databinding.PenemuanFormKlaimBarangBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class PenemuanFormKlaimBarangFragment : Fragment() {

    private var _binding: PenemuanFormKlaimBarangBinding? = null
    private val binding get() = _binding!!

    private var postId: String? = null
    private var imageUri: Uri? = null

    private val calendar = Calendar.getInstance()

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            binding.ivFotoBarangKlaim.setImageURI(it)
            binding.ivFotoBarangKlaim.visibility = View.VISIBLE
            binding.btnUnggahFotoKlaim.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getString("postId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PenemuanFormKlaimBarangBinding.inflate(inflater, container, false)
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
        binding.spinnerKategoriKlaim.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.toolbarKlaim.setOnClickListener { findNavController().navigateUp() }

        binding.tvTanggalHilangKlaim.setOnClickListener { showDatePicker() }
        binding.tvWaktuHilangKlaim.setOnClickListener { showTimePicker() }

        binding.btnUnggahFotoKlaim.setOnClickListener { pickImageLauncher.launch("image/*") }
        binding.btnKirimKlaim.setOnClickListener { validateAndSubmitClaim() }
    }

    private fun showDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            binding.tvTanggalHilangKlaim.text = dateFormat.format(calendar.time)
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
            binding.tvWaktuHilangKlaim.text = timeFormat.format(calendar.time) + " WIB"
        }
        TimePickerDialog(requireContext(), timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE), true).show()
    }

    private fun validateAndSubmitClaim() {
        val namaPengklaim = binding.edtNamaKlaim.text.toString().trim()
        val nimPengklaim = binding.edtNimKlaim.text.toString().trim()
        val namaBarang = binding.edtNamaBarangKlaim.text.toString().trim()
        val deskripsi = binding.edtDeskripsiKlaim.text.toString().trim()

        if (namaPengklaim.isEmpty() || nimPengklaim.isEmpty() || namaBarang.isEmpty() || deskripsi.isEmpty()) {
            Toast.makeText(context, "Harap isi semua kolom wajib!", Toast.LENGTH_SHORT).show()
            return
        }

        if (postId == null) {
            Toast.makeText(context, "Error: ID Postingan tidak ditemukan.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnKirimKlaim.isEnabled = false
        Toast.makeText(context, "Mengirim klaim...", Toast.LENGTH_SHORT).show()

        if (imageUri != null) {
            uploadImageAndSaveClaim()
        } else {
            saveClaimDataToFirestore(null)
        }
    }

    private fun uploadImageAndSaveClaim() {
        val storageRef = storage.reference.child("foto_klaim/${UUID.randomUUID()}.jpg")
        imageUri?.let {
            storageRef.putFile(it)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        saveClaimDataToFirestore(uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Gagal mengunggah gambar: ${e.message}", Toast.LENGTH_SHORT).show()
                    binding.btnKirimKlaim.isEnabled = true
                }
        }
    }

    private fun saveClaimDataToFirestore(imageUrl: String?) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(context, "Anda harus login untuk membuat klaim.", Toast.LENGTH_SHORT).show()
            binding.btnKirimKlaim.isEnabled = true
            return
        }

        val claimData = hashMapOf(
            "uidPengklaim" to user.uid,
            "namaPengklaim" to binding.edtNamaKlaim.text.toString().trim(),
            "nimPengklaim" to binding.edtNimKlaim.text.toString().trim(),
            "namaBarangKlaim" to binding.edtNamaBarangKlaim.text.toString().trim(),
            "kategoriKlaim" to binding.spinnerKategoriKlaim.selectedItem.toString(),
            "deskripsiKlaim" to binding.edtDeskripsiKlaim.text.toString().trim(),
            "tanggalHilangKlaim" to binding.tvTanggalHilangKlaim.text.toString().trim(),
            "waktuHilangKlaim" to binding.tvWaktuHilangKlaim.text.toString().trim(),
            "lokasiHilangKlaim" to binding.edtLokasiKlaim.text.toString().trim(),
            "imageUrlKlaim" to imageUrl,
            "timestampKlaim" to System.currentTimeMillis(),
            "statusKlaim" to "Menunggu Konfirmasi"
        )

        db.collection("form_penemuan").document(postId!!)
            .collection("klaim_barang")
            .add(claimData)
            .addOnSuccessListener {
                Toast.makeText(context, "Klaim berhasil dikirim!", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Gagal mengirim klaim: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.btnKirimKlaim.isEnabled = true
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
