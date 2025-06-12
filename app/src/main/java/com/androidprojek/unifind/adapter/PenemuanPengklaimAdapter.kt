package com.androidprojek.unifind.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidprojek.unifind.databinding.ItemVerifikasiPemilikBinding
import com.androidprojek.unifind.model.PenemuanKlaimModel

// --- PERUBAHAN UTAMA DI KONSTRUKTOR ---
// Kita tidak lagi menggunakan interface, tapi langsung menerima dua fungsi (lambda).
class PenemuanPengklaimAdapter(
    private val listKlaim: List<PenemuanKlaimModel>,
    private val onLihatJawaban: (PenemuanKlaimModel) -> Unit,
    private val onKontak: (PenemuanKlaimModel) -> Unit
) : RecyclerView.Adapter<PenemuanPengklaimAdapter.ViewHolder>() {

    // Interface OnPengklaimActionsListener tidak lagi diperlukan dan bisa dihapus.

    inner class ViewHolder(private val binding: ItemVerifikasiPemilikBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(klaim: PenemuanKlaimModel) {
            binding.tvPengklaimNama.text = klaim.namaPengklaim
            binding.tvPengklaimNim.text = klaim.nimPengklaim
            binding.chipStatusVerifikasi.text = klaim.statusKlaim

            // Panggil lambda secara langsung saat tombol diklik
            binding.btnLihatJawaban.setOnClickListener {
                onLihatJawaban(klaim)
            }
            binding.btnKontakPengklaim.setOnClickListener {
                onKontak(klaim)
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
