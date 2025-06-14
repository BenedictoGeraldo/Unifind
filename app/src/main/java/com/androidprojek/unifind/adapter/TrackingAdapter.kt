package com.androidprojek.unifind.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.androidprojek.unifind.R
import com.androidprojek.unifind.model.Tracking
import com.bumptech.glide.Glide

class TrackingAdapter(
    private val trackingList: List<Tracking>,
    private val onLacakClick: (Tracking) -> Unit
) : RecyclerView.Adapter<TrackingAdapter.TrackingViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tracking, parent, false)
        return TrackingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackingViewHolder, position: Int) {
        val tracking = trackingList[position]
        val isExpanded = expandedPositions.contains(position)

        // === Compact view ===
        holder.tvNamaBarang.text = tracking.namaBarang
        holder.tvKategoriBarang.text = "Kategori: ${tracking.kategoriBarang}"
        if (tracking.imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(tracking.imageUrl)
                .into(holder.ivTrackingImage)
        } else {
            holder.ivTrackingImage.setImageResource(R.drawable.ic_dashboard_black_24dp)
        }

        // === Expanded view ===
        holder.tvNamaBarangExpand.text = tracking.namaBarang
        holder.tvKategoriBarangExpand.text = "${tracking.kategoriBarang}"
        holder.tvDeskripsi.text = "${tracking.deskripsiBarang}"
        holder.idPerangkat.text = "${tracking.idPerangkat}"
        if (tracking.imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(tracking.imageUrl)
                .into(holder.ivTrackingImageExpand)
        } else {
            holder.ivTrackingImageExpand.setImageResource(R.drawable.ic_dashboard_black_24dp)
        }

        // === Visibility toggle ===
        holder.compactLayout.visibility = if (isExpanded) View.GONE else View.VISIBLE
        holder.expandedLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.btnDropdown.setImageResource(
            if (isExpanded) R.drawable.arrow_up else R.drawable.arrow_down
        )

        // Expand/collapse behavior
        holder.btnDropdown.setOnClickListener {
            if (isExpanded) expandedPositions.remove(position)
            else expandedPositions.add(position)
            notifyItemChanged(position)
        }

        // Handle "Lacak" button in both states
        holder.btnLacakCompact.setOnClickListener {
            val bundle = Bundle().apply {
                putString("idPerangkat", tracking.idPerangkat)
            }
            it.findNavController().navigate(R.id.detailTrackingFragment, bundle)
        }

        holder.btnLacakExpand.setOnClickListener {
            val bundle = Bundle().apply {
                putString("idPerangkat", tracking.idPerangkat)
            }
            it.findNavController().navigate(R.id.detailTrackingFragment, bundle)
        }
    }

    override fun getItemCount(): Int = trackingList.size

    inner class TrackingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Compact layout views
        val compactLayout: View = itemView.findViewById(R.id.compact_layout)
        val ivTrackingImage: ImageView = itemView.findViewById(R.id.iv_tracking_image)
        val tvNamaBarang: TextView = itemView.findViewById(R.id.tv_nama_barang)
        val tvKategoriBarang: TextView = itemView.findViewById(R.id.tv_kategori_barang)
        val btnLacakCompact: View = itemView.findViewById(R.id.btn_lacak_compact)

        // Expanded layout views
        val expandedLayout: View = itemView.findViewById(R.id.expanded_layout)
        val tvNamaBarangExpand: TextView = itemView.findViewById(R.id.tv_nama_barang_expand)
        val tvKategoriBarangExpand: TextView = itemView.findViewById(R.id.tv_kategori_barang_expand)
        val ivTrackingImageExpand: ImageView = itemView.findViewById(R.id.iv_tracking_image_expand)
        val tvDeskripsi: TextView = itemView.findViewById(R.id.tv_deskripsi)
        val idPerangkat: TextView = itemView.findViewById(R.id.idPerangkat)
        val btnLacakExpand: View = itemView.findViewById(R.id.btn_lacak_expand)

        // Dropdown button
        val btnDropdown: ImageView = itemView.findViewById(R.id.btn_dropdown)
    }
}