package com.androidprojek.unifind.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidprojek.unifind.R
import com.androidprojek.unifind.databinding.ItemLacakFormulirPencarianBinding
import com.androidprojek.unifind.model.PencarianLacakFormulirModel
import com.bumptech.glide.Glide

class PencarianLacakAdapter(private var listLacak: List<PencarianLacakFormulirModel>) :
    RecyclerView.Adapter<PencarianLacakAdapter.LacakViewHolder>() {

    inner class LacakViewHolder(val binding: ItemLacakFormulirPencarianBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LacakViewHolder {
        val binding = ItemLacakFormulirPencarianBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LacakViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LacakViewHolder, position: Int) {
        val item = listLacak[position]
        holder.binding.apply {
            // Mengisi data teks sesuai format baru
            tvLacakNamaBarang.text = item.namaBarang
            tvLacakNamaPoster.text = "Barang Hilang Milik: ${item.namaPoster}"

            // Memuat gambar barang menggunakan Glide
            Glide.with(holder.itemView.context)
                .load(item.imageUrlPostingan)
                .placeholder(R.drawable.baseline_image_24) // Gambar default saat loading
                .error(R.drawable.baseline_image_24)     // Gambar default jika ada error
                .into(ivLacakFotoBarang)

            // Mengatur teks dan warna Chip status
            chipLacakStatus.text = item.statusLaporan
            val context = holder.itemView.context
            when (item.statusLaporan) {
                "Disetujui" -> {
                    chipLacakStatus.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_100))
                    chipLacakStatus.setTextColor(ContextCompat.getColor(context, R.color.green_700))
                }
                "Ditolak" -> {
                    chipLacakStatus.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red_100))
                    chipLacakStatus.setTextColor(ContextCompat.getColor(context, R.color.red_700))
                }
                else -> { // Menunggu Verifikasi
                    chipLacakStatus.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue_100))
                    chipLacakStatus.setTextColor(ContextCompat.getColor(context, R.color.blue_700))
                }
            }
        }
    }

    override fun getItemCount(): Int = listLacak.size

    fun updateData(newList: List<PencarianLacakFormulirModel>) {
        listLacak = newList
        notifyDataSetChanged()
    }
}