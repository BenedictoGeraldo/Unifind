package com.androidprojek.unifind.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidprojek.unifind.databinding.ItemLaporanMasukBinding
import com.androidprojek.unifind.model.LaporanPenemuanModel

class LaporanMasukAdapter(private val listLaporan: List<LaporanPenemuanModel>) :
    RecyclerView.Adapter<LaporanMasukAdapter.LaporanViewHolder>() {

    // Listener untuk tombol detail
    var onDetailClickListener: ((LaporanPenemuanModel) -> Unit)? = null
    // --- TAMBAHKAN LISTENER BARU UNTUK TOMBOL HUBUNGI ---
    var onHubungiClickListener: ((LaporanPenemuanModel) -> Unit)? = null

    inner class LaporanViewHolder(val binding: ItemLaporanMasukBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanViewHolder {
        val binding = ItemLaporanMasukBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LaporanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LaporanViewHolder, position: Int) {
        val laporan = listLaporan[position]
        holder.binding.apply {
            tvNamaPenemu.text = laporan.penemuNama
            tvNimPenemu.text = laporan.penemuNim
            tvStatusLaporan.text = "Status: ${laporan.statusLaporan}"

            btnLihatDetailLaporan.setOnClickListener {
                onDetailClickListener?.invoke(laporan)
            }

            // --- SET LISTENER UNTUK TOMBOL BARU ---
            btnHubungiPenemu.setOnClickListener {
                onHubungiClickListener?.invoke(laporan)
            }
        }
    }

    override fun getItemCount(): Int = listLaporan.size
}