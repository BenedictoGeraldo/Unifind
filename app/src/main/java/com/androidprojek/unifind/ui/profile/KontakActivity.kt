package com.androidprojek.unifind.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidprojek.unifind.databinding.ActivityKontakBinding
import com.androidprojek.unifind.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class KontakActivity : AppCompatActivity() {

    // Gunakan ViewBinding agar lebih aman dan bersih
    private lateinit var binding: ActivityKontakBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKontakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Fungsi untuk tombol kembali di toolbar
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // Ambil data kontak yang sudah ada dan tampilkan
        loadContactData()

        // Beri logika pada tombol simpan
        binding.btnSimpan.setOnClickListener {
            saveContactData()
        }
    }

    private fun loadContactData() {
        setLoading(true)
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Sesi tidak valid, silakan login ulang.", Toast.LENGTH_SHORT).show()
            setLoading(false)
            finish()
            return
        }

        // Ambil data dari collection "users" berdasarkan UID pengguna yang login
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userProfile = document.toObject(UserModel::class.java)
                    // Tampilkan data yang sudah ada ke EditText
                    binding.etInstagram.setText(userProfile?.instagram)
                    binding.etLine.setText(userProfile?.line)
                    binding.etWhatsapp.setText(userProfile?.whatsapp)
                }
                setLoading(false)
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Gagal memuat data kontak: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveContactData() {
        val instagram = binding.etInstagram.text.toString().trim()
        val line = binding.etLine.text.toString().trim()
        val whatsapp = binding.etWhatsapp.text.toString().trim()

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Sesi tidak valid, silakan login ulang.", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        // Siapkan data yang akan di-update dalam bentuk Map
        val contactData = mapOf(
            "instagram" to instagram,
            "line" to line,
            "whatsapp" to whatsapp
        )

        // Gunakan .update() untuk hanya mengubah field-field ini di dokumen pengguna
        db.collection("users").document(user.uid)
            .update(contactData)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, "Kontak berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                finish() // Kembali ke halaman profil setelah berhasil
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Gagal memperbarui kontak: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSimpan.isEnabled = !isLoading
    }
}