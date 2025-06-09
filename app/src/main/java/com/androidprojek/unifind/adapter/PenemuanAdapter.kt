package com.androidprojek.unifind.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.androidprojek.unifind.R
import com.androidprojek.unifind.databinding.ItemPenemuanBinding
import com.androidprojek.unifind.model.PenemuanModel
import com.bumptech.glide.Glide

// Pastikan parameter konstruktor adalah MutableList
class PenemuanAdapter(private var listPenemuan: MutableList<PenemuanModel>) : RecyclerView.Adapter<PenemuanAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemPenemuanBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(penemuan: PenemuanModel) {
            // Mengisi data dari model ke view
            binding.tvNamaPenemu.text = penemuan.namaPelapor
            binding.tvNimPenemu.text = penemuan.nim
            binding.tvNamaBarang.text = penemuan.namaBarang
            binding.tvStatus.text = "Dalam Pencarian" // Diatur statis

            // Mengatur gambar profil (placeholder)
            binding.ivFotoProfil.setImageResource(R.drawable.ic_launcher_background)

            // Mengatur gambar barang (ViewPager2)
            penemuan.imageUrl?.let { url ->
                if (url.isNotEmpty()) {
                    val imageUrlList = listOf(url)
                    val imageAdapter = ImageSliderAdapter(imageUrlList)
                    binding.viewPagerGambarBarang.adapter = imageAdapter
                    binding.dotsIndicator.visibility = View.GONE
                    binding.viewPagerGambarBarang.visibility = View.VISIBLE
                } else {
                    binding.viewPagerGambarBarang.visibility = View.GONE
                    binding.dotsIndicator.visibility = View.GONE
                }
            } ?: run {
                binding.viewPagerGambarBarang.visibility = View.GONE
                binding.dotsIndicator.visibility = View.GONE
            }

            // Mengisi data di bagian detail
            binding.tvDetailNamaBarang.text = penemuan.namaBarang
            binding.tvDetailKategori.text = penemuan.kategori
            binding.tvDetailDeskripsi.text = penemuan.deskripsi
            binding.tvDetailTanggal.text = penemuan.tanggalPenemuan
            binding.tvDetailWaktu.text = penemuan.waktuPenemuan
            binding.tvDetailLokasi.text = penemuan.lokasiPenemuan

            // Mengatur logika untuk tombol expand/collapse
            setupToggleButton(binding)
        }

        private fun setupToggleButton(binding: ItemPenemuanBinding) {
            binding.btnKiri.text = "Detail Barang"
            binding.btnKanan.text = "Klaim Barang"

            binding.ivToggleDetail.setOnClickListener {
                val isVisible = binding.layoutDetail.visibility == View.VISIBLE
                TransitionManager.beginDelayedTransition(binding.root as ViewGroup)

                if (isVisible) {
                    binding.layoutDetail.visibility = View.GONE
                    binding.ivToggleDetail.setImageResource(R.drawable.ic_arrow_down)
                    binding.btnKiri.text = "Detail Barang"
                } else {
                    binding.layoutDetail.visibility = View.VISIBLE
                    binding.ivToggleDetail.setImageResource(R.drawable.ic_arrow_up)
                    binding.btnKiri.text = "Tutup Detail"
                }
            }

            binding.btnKiri.setOnClickListener {
                binding.ivToggleDetail.performClick()
            }

            binding.btnKanan.setOnClickListener {
                // TODO: Tambahkan logika untuk proses klaim barang di sini
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPenemuanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listPenemuan[position])
    }

    override fun getItemCount(): Int = listPenemuan.size

}