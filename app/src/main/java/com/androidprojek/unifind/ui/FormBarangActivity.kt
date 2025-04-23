package com.androidprojek.unifind.ui

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidprojek.unifind.databinding.ActivityFormBarangBinding
import com.androidprojek.unifind.model.BarangModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FormBarangActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBarangBinding
    private var imageUri: Uri? = null

    companion object {
        const val PICK_IMAGE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUploadGambar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }

        binding.btnSimpan.setOnClickListener {
            if (imageUri == null) {
                Toast.makeText(this, "Silakan pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val barang = BarangModel(
                nama = binding.etNama.text.toString(),
                nim = binding.etNim.text.toString(),
                namaBarang = binding.etNamaBarang.text.toString(),
                kategori = binding.etKategori.text.toString(),
                deskripsi = binding.etDeskripsi.text.toString(),
                tanggalHilang = binding.etTanggal.text.toString(),
                waktuHilang = binding.etWaktu.text.toString(),
                lokasiHilang = binding.etLokasi.text.toString(),
                fotoUri = imageUri.toString()
            )

            val resultIntent = Intent()
            resultIntent.putExtra("DATA_BARANG", barang)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data

            val inputStream = contentResolver.openInputStream(imageUri!!)
            val file = File(cacheDir, "img_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            imageUri = Uri.fromFile(file)

            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            binding.imgPreview.setImageBitmap(bitmap)

        }
    }
}
