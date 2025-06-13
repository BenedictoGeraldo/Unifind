package com.androidprojek.unifind.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
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
