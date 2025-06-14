package com.androidprojek.unifind.ui.dashboard

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androidprojek.unifind.R
import com.androidprojek.unifind.databinding.FragmentAddTrackingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddTrackingFragment : Fragment() {

    private var _binding: FragmentAddTrackingBinding? = null
    private val binding get() = _binding!!

    private var selectedCategory: String? = null
    private var imageUri: Uri? = null

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Launcher untuk memilih gambar dari galeri
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // Tampilkan gambar di ImageView
            binding.ivFotoBarang.setImageURI(it)
            binding.ivFotoBarang.visibility = View.VISIBLE
            // Sembunyikan ikon unggah dan petunjuk setelah gambar dipilih
            binding.unggahBg.visibility = View.GONE
            // Simpan URI gambar untuk digunakan nanti (misalnya untuk upload ke server)
            // Anda bisa menyimpan URI ini di variabel atau ViewModel
            imageUri = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (auth.currentUser == null) {
            Log.d("Auth", "User not logged in")
            findNavController().navigate(R.id.navigation_home)
            return
        } else {
            Log.d("Auth", "User logged in with UID: ${auth.currentUser?.uid}")
        }

        // Atur navigasi kembali pada tombol Back
        binding.back.setOnClickListener {
            findNavController().navigateUp() // Kembali ke halaman sebelumnya (menu Pelacakan)
        }

        // Setup Spinner untuk kategori barang
        setupSpinner()

        // Atur listener untuk tombol Unggah Foto
        binding.btnUploadFoto.setOnClickListener {
            // Buka galeri untuk memilih gambar
            pickImageLauncher.launch("image/*")
        }

        // Atur listener untuk tombol Hubungkan
        binding.btnHubungkan.setOnClickListener {
            val namaBarang = binding.edtNama.text.toString()
            val kategoriBarang = selectedCategory
            val deskripsiBarang = binding.edtDeskripsi.text.toString()
            val idPerangkat = binding.edtId.text.toString()

            // Validasi field wajib
            if (namaBarang.isEmpty() || kategoriBarang == null || deskripsiBarang.isEmpty()) {
                Toast.makeText(context, "Lengkapi semua field wajib", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Jika ada gambar, unggah ke Firebase Storage
            if (imageUri != null) {
                val storageRef = storage.reference.child("tracking_images/${UUID.randomUUID()}.jpg")
                storageRef.putFile(imageUri!!)
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { throw it }
                        }
                        storageRef.downloadUrl
                    }
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        saveToFirestore(namaBarang, kategoriBarang, deskripsiBarang, idPerangkat, imageUrl)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Gagal mengunggah gambar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Simpan data ke Firestore tanpa gambar
                saveToFirestore(namaBarang, kategoriBarang, deskripsiBarang, idPerangkat, null)
            }
        }
    }

    private fun saveToFirestore(
        namaBarang: String,
        kategoriBarang: String,
        deskripsiBarang: String,
        idPerangkat: String,
        imageUrl: String?
    ) {
        // Mengambil User Id Saat Ini
        val userId = auth.currentUser?.uid

        // Buat data yang akan disimpan
        val trackingData = hashMapOf(
            "namaBarang" to namaBarang,
            "kategoriBarang" to kategoriBarang,
            "deskripsiBarang" to deskripsiBarang,
            "idPerangkat" to idPerangkat,
            "imageUrl" to imageUrl,
            "timestamp" to System.currentTimeMillis(),
            "userId" to userId
        )

        // Simpan ke Firestore di collection "trackings"
        db.collection("trackings")
            .add(trackingData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(context, "Data pelacakan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                // Kembali ke halaman sebelumnya (menu Pelacakan)
                findNavController().navigateUp()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSpinner() {
        // Ambil daftar kategori dari arrays.xml
        val categories = resources.getStringArray(R.array.kategori_barang).toMutableList()

        // Buat adapter untuk Spinner
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Nonaktifkan item pertama (placeholder)
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextColor(requireContext().getColor(android.R.color.darker_gray))
                } else {
                    view.setTextColor(requireContext().getColor(android.R.color.black))
                }
                return view
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                if (position == 0 && selectedCategory == null) {
                    view.setTextColor(requireContext().getColor(android.R.color.darker_gray))
                } else {
                    view.setTextColor(requireContext().getColor(android.R.color.black))
                }
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerKategoriBarang.adapter = adapter

        binding.spinnerKategoriBarang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    selectedCategory = null
                } else {
                    selectedCategory = categories[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCategory = null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}