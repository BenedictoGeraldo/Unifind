package com.androidprojek.unifind.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidprojek.unifind.R
import com.androidprojek.unifind.databinding.ItemVerifikasiPemilikBinding
import com.androidprojek.unifind.model.PenemuanKlaimModel

// --- PERUBAHAN UTAMA DI KONSTRUKTOR ---
// Kita tidak lagi menggunakan interface, tapi langsung menerima dua fungsi (lambda).
class PenemuanPengklaimAdapter(
    private val listKlaim: List<PenemuanKlaimModel>,
    private val onLihatJawaban: (PenemuanKlaimModel) -> Unit,
    private val onKontak: (PenemuanKlaimModel) -> Unit
) : RecyclerView.Adapter<PenemuanPengklaimAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemVerifikasiPemilikBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(klaim: PenemuanKlaimModel) {
            binding.tvPengklaimNama.text = klaim.namaPengklaim
            binding.tvPengklaimNim.text = klaim.nimPengklaim
            binding.chipStatusVerifikasi.text = klaim.statusKlaim

            // --- LOGIKA UNTUK MENGUBAH WARNA BERDASARKAN STATUS ---
            val context = itemView.context
            when (klaim.statusKlaim) {
                "Diterima" -> {
                    // Tampilan untuk status DITERIMA
                    binding.chipStatusVerifikasi.setChipBackgroundColorResource(R.color.status_diterima_bg)
                    binding.chipStatusVerifikasi.setTextColor(ContextCompat.getColor(context, R.color.status_text_color))

                    binding.btnKontakPengklaim.isEnabled = true
                    binding.btnKontakPengklaim.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.kontak_diterima_bg))
                    binding.btnKontakPengklaim.setTextColor(ContextCompat.getColor(context, R.color.status_text_color))
                    binding.btnKontakPengklaim.setIconTintResource(R.color.status_text_color)
                }
                "Ditolak" -> {
                    // Tampilan untuk status DITOLAK
                    binding.chipStatusVerifikasi.setChipBackgroundColorResource(R.color.status_ditolak_bg)
                    binding.chipStatusVerifikasi.setTextColor(ContextCompat.getColor(context, R.color.status_text_color))

                    binding.btnKontakPengklaim.isEnabled = false
                    binding.btnKontakPengklaim.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.kontak_ditolak_bg))
                    binding.btnKontakPengklaim.setTextColor(ContextCompat.getColor(context, R.color.status_text_color))
                    binding.btnKontakPengklaim.setIconTintResource(R.color.status_text_color)
                }
                else -> { // Default untuk "Menunggu Konfirmasi"
                    // Tampilan DEFAULT
                    binding.chipStatusVerifikasi.setChipBackgroundColorResource(R.color.grey_pending_bg)
                    binding.chipStatusVerifikasi.setTextColor(ContextCompat.getColor(context, R.color.grey_pending_text))

                    binding.btnKontakPengklaim.isEnabled = false
                    binding.btnKontakPengklaim.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.kontak_ditolak_bg))
                    binding.btnKontakPengklaim.setTextColor(ContextCompat.getColor(context, R.color.status_text_color))
                    binding.btnKontakPengklaim.setIconTintResource(R.color.status_text_color)
                }
            }

            // Panggil lambda secara langsung saat tombol diklik
            binding.btnLihatJawaban.setOnClickListener {
                onLihatJawaban(klaim)
            }
            binding.btnKontakPengklaim.setOnClickListener {
                if (it.isEnabled) {
                    onKontak(klaim)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVerifikasiPemilikBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listKlaim[position])
    }

    override fun getItemCount(): Int = listKlaim.size
}
