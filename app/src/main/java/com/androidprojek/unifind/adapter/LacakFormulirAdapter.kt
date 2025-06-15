package com.androidprojek.unifind.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidprojek.unifind.R
import com.androidprojek.unifind.databinding.PenemuanItemLacakFormulirBinding
import com.androidprojek.unifind.model.PenemuanLacakFormulirModel
import com.bumptech.glide.Glide

class LacakFormulirAdapter(
    private var listLacak: List<PenemuanLacakFormulirModel>
) : RecyclerView.Adapter<LacakFormulirAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: PenemuanItemLacakFormulirBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PenemuanLacakFormulirModel) {
            binding.tvLacakNamaBarang.text = item.namaBarangPostingan
            binding.tvLacakNamaPenemu.text = "Ditemukan oleh: ${item.namaPenemu}"
            binding.chipLacakStatus.text = item.statusKlaim

            Glide.with(itemView.context)
                .load(item.imageUrlPostingan)
                .placeholder(R.drawable.baseline_image_24)
                .into(binding.ivLacakFotoBarang)

            // --- LOGIKA UNTUK MENGUBAH WARNA STATUS CHIP ---
            val context = itemView.context
            when (item.statusKlaim) {
                "Diterima" -> {
                    // Tampilan untuk status DITERIMA
                    binding.chipLacakStatus.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_diterima_bg))
                    binding.chipLacakStatus.setTextColor(ContextCompat.getColor(context, R.color.status_text_color))
                }
                "Ditolak" -> {
                    // Tampilan untuk status DITOLAK
                    binding.chipLacakStatus.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_ditolak_bg))
                    binding.chipLacakStatus.setTextColor(ContextCompat.getColor(context, R.color.status_text_color))
                }
                else -> { // Default untuk "Menunggu Konfirmasi"
                    // Tampilan DEFAULT
                    binding.chipLacakStatus.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey_pending_bg))
                    binding.chipLacakStatus.setTextColor(ContextCompat.getColor(context, R.color.grey_pending_text))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PenemuanItemLacakFormulirBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listLacak[position])
    }

    override fun getItemCount(): Int = listLacak.size

    fun updateData(newList: List<PenemuanLacakFormulirModel>) {
        listLacak = newList
        notifyDataSetChanged()
    }
}
