package com.androidprojek.unifind

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.androidprojek.unifind.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false // untuk toggle password

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        // =======================
        // TOGGLE VISIBILITY PASSWORD
        // =======================
        val passwordField = binding.PasswordField
        val eyeIcon = findViewById<ImageView>(R.id.eye_icon) // pastikan id di XML adalah eye_icon

        eyeIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordField.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                eyeIcon.setImageResource(R.drawable.eye_icon) // opsional: ubah ke ikon "eye open"
            } else {
                passwordField.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                eyeIcon.setImageResource(R.drawable.eye_icon) // kembali ke ikon default
            }
            // Move cursor to the end
            passwordField.setSelection(passwordField.text.length)
        }

        // =======================
        // LOGIN BUTTON
        // =======================
        binding.btnLogin.setOnClickListener{
            val nim = binding.NimField.text.toString()
            val email = "$nim@mahasiswa.upnvj.ac.id"
            val password = binding.PasswordField.text.toString()

            // Reset error visibility setiap kali tombol diklik
            binding.nimErr.visibility = android.view.View.GONE
            binding.passErr.visibility = android.view.View.GONE

            var hasError = false

            // Validasi input kosong
            if (email.isEmpty()) {
                binding.txtNimErr.text = "Field ini wajib diisi!"
                binding.nimErr.visibility = android.view.View.VISIBLE
                hasError = true
            }

            if (password.isEmpty()) {
                binding.txtPassErr.text = "Field ini wajib diisi!"
                binding.passErr.visibility = android.view.View.VISIBLE
                hasError = true
            }

            if (hasError) return@setOnClickListener

            // Coba login ke Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login berhasil
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        // Login gagal, kita cek error-nya
                        val errorMessage = task.exception?.message

                        // Contoh handling berdasarkan isi error (bisa disesuaikan)
                        if (errorMessage != null) {
                            if (errorMessage?.contains("no user record") == true || errorMessage.contains("There is no user")) {
                                binding.txtNimErr.text = "NIM tidak terdaftar"
                                binding.nimErr.visibility = android.view.View.VISIBLE
                            } else if (errorMessage?.contains("password is invalid") == true) {
                                binding.txtPassErr.text = "Password tidak sesuai"
                                binding.passErr.visibility = android.view.View.VISIBLE
                            } else {
                                binding.txtPassErr.text = "Gagal masuk, periksa kembali NIM dan password"
                                binding.passErr.visibility = android.view.View.VISIBLE
                            }
                        }
                    }
                }
        }
    }
}