package com.androidprojek.unifind.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidprojek.unifind.databinding.ItemKategoriFilterBinding
import com.androidprojek.unifind.model.KategoriModel // Pastikan ini mengimpor dari package model

class KategoriFilterAdapter(
    private val listKategori: List<KategoriModel>,
    private val onCategoryClick: (KategoriModel) -> Unit
) : RecyclerView.Adapter<KategoriFilterAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemKategoriFilterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(kategori: KategoriModel) {
            binding.chipKategori.text = kategori.nama
            binding.chipKategori.isChecked = kategori.isSelected

            binding.chipKategori.setOnClickListener {
                onCategoryClick(kategori)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKategoriFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listKategori[position])
    }

    override fun getItemCount(): Int = listKategori.size
}