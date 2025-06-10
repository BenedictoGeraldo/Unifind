package com.androidprojek.unifind

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidprojek.unifind.databinding.ActivityRegisterBinding
import com.androidprojek.unifind.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val nama = binding.etNama.text.toString().trim()
        val nim = binding.etNim.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Validasi input dasar
        if (nama.isEmpty() || nim.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        // LANGKAH 1: Cek dulu ke Firestore apakah NIM sudah ada di collection 'users'
        db.collection("users").whereEqualTo("nim", nim).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Jika hasil pencarian kosong, berarti NIM belum terdaftar. Lanjutkan pendaftaran.
                    createFirebaseUser(nama, nim, password)
                } else {
                    // Jika ada dokumen yang ditemukan, berarti NIM sudah terdaftar.
                    setLoading(false)
                    Toast.makeText(this, "NIM ini sudah terdaftar.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Gagal memverifikasi NIM: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun createFirebaseUser(nama: String, nim: String, password: String) {
        val email = "$nim@mahasiswa.upnvj.ac.id"

        // LANGKAH 2: Buat user di Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // LANGKAH 3: Simpan profil ke Firestore
                    saveUserProfile(uid = task.result.user!!.uid, nama = nama, nim = nim, email = email)
                } else {
                    setLoading(false)
                    // Cek jenis errornya (sebagai fallback jika terjadi race condition)
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "NIM ini sudah terdaftar.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Gagal mendaftar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun saveUserProfile(uid: String, nama: String, nim: String, email: String) {
        val user = UserModel(
            uid = uid,
            nama = nama,
            nim = nim,
            email = email
        )

        // 2. Simpan objek user ke Cloud Firestore di collection "users"
        db.collection("users").document(uid).set(user)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, "Pendaftaran berhasil! Silakan login.", Toast.LENGTH_LONG).show()
                finish() // Kembali ke halaman Login
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Gagal menyimpan profil: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }
}