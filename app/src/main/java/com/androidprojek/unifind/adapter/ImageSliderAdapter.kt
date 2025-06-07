package com.androidprojek.unifind.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidprojek.unifind.databinding.ItemGambarSliderBinding // Buat layout XML ini
import com.bumptech.glide.Glide

class ImageSliderAdapter(private val imageUrls: List<String>) : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ItemGambarSliderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemGambarSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop()
            .into(holder.binding.ivGambarSlider)
    }

    override fun getItemCount(): Int = imageUrls.size
}