package com.androidprojek.unifind.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidprojek.unifind.databinding.ActivityKontakPelaporBinding

class KontakPelaporActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKontakPelaporBinding

    // Definisikan 'key' untuk intent agar konsisten dan aman dari typo
    companion object {
        const val EXTRA_INSTAGRAM = "extra_instagram"
        const val EXTRA_LINE = "extra_line"
        const val EXTRA_WHATSAPP = "extra_whatsapp"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKontakPelaporBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol kembali di toolbar
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // Ambil data yang dikirim dari adapter
        val instagram = intent.getStringExtra(EXTRA_INSTAGRAM)
        val line = intent.getStringExtra(EXTRA_LINE)
        val whatsapp = intent.getStringExtra(EXTRA_WHATSAPP)

        // Tampilkan data ke UI dan atur visibilitas
        setupContactView(binding.layoutInstagram, binding.tvInstagram, instagram)
        setupContactView(binding.layoutLine, binding.tvLine, line)
        setupContactView(binding.layoutWhatsapp, binding.tvWhatsapp, whatsapp)

        // Atur listener untuk membuka aplikasi terkait
        setupClickListeners(instagram, line, whatsapp)
    }

    private fun setupContactView(layout: View, textView: android.widget.TextView, data: String?) {
        if (!data.isNullOrEmpty()) {
            layout.visibility = View.VISIBLE
            textView.text = data
        } else {
            // Sembunyikan baris kontak jika datanya tidak ada
            layout.visibility = View.GONE
        }
    }

    private fun setupClickListeners(instagram: String?, line: String?, whatsapp: String?) {
        binding.layoutInstagram.setOnClickListener {
            if (!instagram.isNullOrEmpty()) {
                val username = instagram.removePrefix("@")
                val uri = Uri.parse("http://instagram.com/_u/$username")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Aplikasi Instagram tidak terpasang.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.layoutLine.setOnClickListener {
            if (!line.isNullOrEmpty()) {
                val uri = Uri.parse("https://line.me/R/ti/p/~$line")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Aplikasi Line tidak terpasang.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.layoutWhatsapp.setOnClickListener {
            if (!whatsapp.isNullOrEmpty()) {
                // Format nomor ke standar internasional tanpa + atau 0 di depan
                val formattedNumber = whatsapp.replaceFirst("^0", "").replace(Regex("[^0-9]"), "")
                val url = "https://wa.me/62$formattedNumber"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Aplikasi WhatsApp tidak terpasang.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}