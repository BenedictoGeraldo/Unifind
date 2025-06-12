package com.androidprojek.unifind.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.R
import com.androidprojek.unifind.adapter.LaporanMasukAdapter
import com.androidprojek.unifind.databinding.ActivityLaporanMasukBinding
import com.androidprojek.unifind.model.LaporanPenemuanModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LaporanMasukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaporanMasukBinding
    private lateinit var adapter: LaporanMasukAdapter
    private val listLaporan = mutableListOf<LaporanPenemuanModel>()
    private val db = FirebaseFirestore.getInstance()
    private var idBarangHilang: String? = null

    companion object {
        const val EXTRA_BARANG_ID = "extra_barang_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaporanMasukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idBarangHilang = intent.getStringExtra(EXTRA_BARANG_ID)
        if (idBarangHilang.isNullOrEmpty()) { // Pemeriksaan yang lebih aman
            Toast.makeText(this, "ID Barang tidak valid.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        fetchLaporanPenemuan()
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = LaporanMasukAdapter(listLaporan)
        binding.rvDaftarLaporan.layoutManager = LinearLayoutManager(this)
        binding.rvDaftarLaporan.adapter = adapter

        adapter.onDetailClickListener = { detailLaporan ->
            val intent = Intent(this, VerifikasiLaporanMasukActivity::class.java).apply {
                putExtra(VerifikasiLaporanMasukActivity.EXTRA_LAPORAN, detailLaporan)
            }
            startActivity(intent)
        }
        // --- IMPLEMENTASIKAN LISTENER BARU DI SINI ---
        adapter.onHubungiClickListener = { laporan ->
            showKontakDialog(laporan)
        }
    }

    // --- TAMBAHKAN FUNGSI BARU INI UNTUK MENAMPILKAN DIALOG ---
    private fun showKontakDialog(laporan: LaporanPenemuanModel) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_kontak_penemu, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()

        // Ambil view dari layout dialog
        val btnClose = dialogView.findViewById<ImageView>(R.id.btn_close_dialog)

        val layoutInstagram = dialogView.findViewById<LinearLayout>(R.id.layout_instagram)
        val tvInstagram = dialogView.findViewById<TextView>(R.id.tv_instagram)

        val layoutLine = dialogView.findViewById<LinearLayout>(R.id.layout_line)
        val tvLine = dialogView.findViewById<TextView>(R.id.tv_line)

        val layoutWhatsapp = dialogView.findViewById<LinearLayout>(R.id.layout_whatsapp)
        val tvWhatsapp = dialogView.findViewById<TextView>(R.id.tv_whatsapp)

        // Setup tampilan kontak (kita 'pinjam' logika dari KontakPelaporActivity)
        setupDialogContactView(layoutInstagram, tvInstagram, laporan.penemuInstagram)
        setupDialogContactView(layoutLine, tvLine, laporan.penemuLine)
        setupDialogContactView(layoutWhatsapp, tvWhatsapp, laporan.penemuWhatsapp)

        // Setup klik listener (kita 'pinjam' lagi logikanya)
        setupDialogClickListeners(
            layoutInstagram, laporan.penemuInstagram,
            layoutLine, laporan.penemuLine,
            layoutWhatsapp, laporan.penemuWhatsapp
        )

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        // Atur agar background dialog transparan sehingga CardView bisa terlihat rounded
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    // Fungsi helper untuk dialog, diadaptasi dari KontakPelaporActivity
    private fun setupDialogContactView(layout: View, textView: TextView, data: String?) {
        if (!data.isNullOrEmpty()) {
            layout.visibility = View.VISIBLE
            textView.text = data
        } else {
            layout.visibility = View.GONE
        }
    }

    // Fungsi helper untuk dialog, diadaptasi dari KontakPelaporActivity
    private fun setupDialogClickListeners(
        layoutInstagram: View, instagram: String?,
        layoutLine: View, line: String?,
        layoutWhatsapp: View, whatsapp: String?
    ) {
        layoutInstagram.setOnClickListener {
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

        layoutLine.setOnClickListener {
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

        layoutWhatsapp.setOnClickListener {
            if (!whatsapp.isNullOrEmpty()) {
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

    private fun fetchLaporanPenemuan() {
        binding.tvEmptyLaporan.visibility = View.VISIBLE // Tampilkan teks "kosong" di awal
        binding.rvDaftarLaporan.visibility = View.GONE

        db.collection("barangHilang").document(idBarangHilang!!)
            .collection("laporanPenemuan")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Query ini sekarang aman
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    // Ganti Log Tag agar konsisten dengan nama Activity
                    Log.w("LaporanMasukActivity", "Error listening for documents.", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    listLaporan.clear()

                    // --- PERUBAHAN LOGIKA PENGAMBILAN DATA ---
                    // Loop manual untuk mendapatkan ID setiap laporan
                    for (doc in snapshots.documents) {
                        val laporan = doc.toObject(LaporanPenemuanModel::class.java)
                        if (laporan != null) {
                            laporan.id = doc.id // Masukkan ID dokumen ke dalam model
                            listLaporan.add(laporan)
                        }
                    }
                    // --- AKHIR PERUBAHAN ---

                    adapter.notifyDataSetChanged()

                    // Atur visibilitas berdasarkan apakah list kosong atau tidak
                    if (listLaporan.isEmpty()) {
                        binding.tvEmptyLaporan.visibility = View.VISIBLE
                        binding.rvDaftarLaporan.visibility = View.GONE
                    } else {
                        binding.tvEmptyLaporan.visibility = View.GONE
                        binding.rvDaftarLaporan.visibility = View.VISIBLE
                    }
                }
            }
    }
}