package com.androidprojek.unifind.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androidprojek.unifind.R
import com.androidprojek.unifind.databinding.ItemLaporanMasukBinding
import com.androidprojek.unifind.model.LaporanPenemuanModel
import com.bumptech.glide.Glide

class LaporanMasukAdapter(private val listLaporan: List<LaporanPenemuanModel>) :
    RecyclerView.Adapter<LaporanMasukAdapter.LaporanViewHolder>() {

    var onDetailClickListener: ((LaporanPenemuanModel) -> Unit)? = null
    var onHubungiClickListener: ((LaporanPenemuanModel) -> Unit)? = null

    inner class LaporanViewHolder(val binding: ItemLaporanMasukBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanViewHolder {
        val binding = ItemLaporanMasukBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LaporanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LaporanViewHolder, position: Int) {
        val laporan = listLaporan[position]
        holder.binding.apply {
            // Mengikat data ke TextViews
            tvNamaPenemu.text = laporan.penemuNama
            tvNimPenemu.text = laporan.penemuNim

            // Memuat foto profil penemu ke CircleImageView
            Glide.with(holder.itemView.context)
                .load(laporan.penemuPhotoUrl)
                .placeholder(R.drawable.baseline_person_outline_24)
                .error(R.drawable.baseline_person_outline_24)
                .into(ivPenemuProfile)

            // Mengikat data ke Chip status
            chipStatusLaporan.text = laporan.statusLaporan
            val context = holder.itemView.context
            when (laporan.statusLaporan) {
                "Disetujui" -> {
                    chipStatusLaporan.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_100))
                    chipStatusLaporan.setTextColor(ContextCompat.getColor(context, R.color.green_700))
                }
                "Ditolak" -> {
                    chipStatusLaporan.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red_100))
                    chipStatusLaporan.setTextColor(ContextCompat.getColor(context, R.color.red_700))
                }
                else -> { // Menunggu Verifikasi
                    chipStatusLaporan.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue_100))
                    chipStatusLaporan.setTextColor(ContextCompat.getColor(context, R.color.blue_700))
                }
            }

            // Menghubungkan listener ke tombol yang benar
            btnLihatDetailLaporan.setOnClickListener {
                onDetailClickListener?.invoke(laporan)
            }
            btnHubungiPenemu.setOnClickListener {
                onHubungiClickListener?.invoke(laporan)
            }
        }
    }

    override fun getItemCount(): Int = listLaporan.size
}