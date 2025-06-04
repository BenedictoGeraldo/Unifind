package com.androidprojek.unifind.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.androidprojek.unifind.R
import com.androidprojek.unifind.model.Tracking
import com.bumptech.glide.Glide

class TrackingAdapter(private val trackingList: List<Tracking>) :
    RecyclerView.Adapter<TrackingAdapter.TrackingViewHolder>() {

    class TrackingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivTrackingImage: ImageView = itemView.findViewById(R.id.iv_tracking_image)
        val tvNamaBarang: TextView = itemView.findViewById(R.id.tv_nama_barang)
        val tvKategoriBarang: TextView = itemView.findViewById(R.id.tv_kategori_barang)
        val btnDropdown: ImageView = itemView.findViewById(R.id.btn_dropdown)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tracking, parent, false)
        return TrackingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackingViewHolder, position: Int) {
        val tracking = trackingList[position]

        holder.tvNamaBarang.text = tracking.namaBarang
        holder.tvKategoriBarang.text = "Kategori: ${tracking.kategoriBarang}"

        if (tracking.imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(tracking.imageUrl)
                .into(holder.ivTrackingImage)
        } else {
            holder.ivTrackingImage.setImageResource(R.drawable.ic_dashboard_black_24dp)
        }

        holder.btnDropdown.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Dropdown diklik di posisi $position", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = trackingList.size
}