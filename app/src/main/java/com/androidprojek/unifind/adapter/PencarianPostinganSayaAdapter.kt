package com.androidprojek.unifind.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.androidprojek.unifind.databinding.ItemPencarianPostinganSayaBinding // <-- Import View Binding
import com.androidprojek.unifind.model.BarangModel
import com.bumptech.glide.Glide

class PencarianPostinganSayaAdapter(private val listBarang: List<BarangModel>) :
    RecyclerView.Adapter<PencarianPostinganSayaAdapter.PencarianViewHolder>() {

    // Listener untuk menangani klik tombol
    var onLihatLaporanClickListener: ((BarangModel) -> Unit)? = null

    // ViewHolder sekarang menggunakan View Binding, lebih bersih!
    inner class PencarianViewHolder(val binding: ItemPencarianPostinganSayaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PencarianViewHolder {
        // Inflate layout menggunakan View Binding
        val binding = ItemPencarianPostinganSayaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PencarianViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PencarianViewHolder, position: Int) {
        val barang = listBarang[position]

        // Kita akan binding data menggunakan 'holder.binding'
        holder.binding.apply {
            // Info Utama
            tvNamaBarang.text = barang.namaBarang
            tvStatus.text = barang.status

            // Detail Tersembunyi
            tvDetailKategori.text = barang.kategori
            tvDetailDeskripsi.text = barang.deskripsi
            tvDetailTanggal.text = barang.tanggalHilang
            tvDetailWaktu.text = barang.waktuHilang
            tvDetailLokasi.text = barang.lokasiHilang

            // Setup Image Slider (logika sama seperti BarangAdapter)
            if (barang.fotoUris.isNotEmpty()) {
                viewPagerGambarBarang.adapter = ImageSliderAdapter(barang.fotoUris)
                dotsIndicator.attachTo(viewPagerGambarBarang)
                viewPagerGambarBarang.visibility = View.VISIBLE
                dotsIndicator.visibility = View.VISIBLE
            } else {
                viewPagerGambarBarang.visibility = View.GONE
                dotsIndicator.visibility = View.GONE
            }

            // Implementasi expand/collapse detail (logika sama seperti BarangAdapter)
            ivToggleDetail.setOnClickListener {
                val isVisible = layoutDetail.visibility == View.VISIBLE
                if (isVisible) {
                    layoutDetail.visibility = View.GONE
                    ivToggleDetail.animate().rotation(0f).setDuration(200).start()
                } else {
                    layoutDetail.visibility = View.VISIBLE
                    ivToggleDetail.animate().rotation(180f).setDuration(200).start()
                }
            }

            // Set listener untuk tombol "Lihat Laporan Penemuan"
            btnLihatLaporan.setOnClickListener {
                // Panggil listener yang akan diimplementasikan di Fragment
                onLihatLaporanClickListener?.invoke(barang)
            }
        }
    }

    override fun getItemCount(): Int = listBarang.size
}