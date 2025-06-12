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

// --- 1. UBAH INTERFACE ---
// Tambahkan fungsi baru untuk menangani klik "Verifikasi"
interface OnItemClickListener {
    fun onKlaimClick(postId: String)
    fun onVerifikasiClick(postId: String) // Fungsi baru
}

// --- 2. UBAH KONSTRUKTOR ADAPTER ---
// Tambahkan 'isMyPostsPage' untuk menandai konteks halaman.
class PenemuanAdapter(
    private var listPenemuan: MutableList<PenemuanModel>,
    private val listener: OnItemClickListener,
    private val isMyPostsPage: Boolean = false // Defaultnya false (untuk halaman Home)
) : RecyclerView.Adapter<PenemuanAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemPenemuanBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(penemuan: PenemuanModel) {
            // Bagian ini tidak ada perubahan
            binding.tvNamaPenemu.text = penemuan.namaPelapor
            binding.tvNimPenemu.text = penemuan.nim
            binding.tvNamaBarang.text = penemuan.namaBarang
            binding.tvStatus.text = "Dalam Pencarian"
            binding.ivFotoProfil.setImageResource(R.drawable.ic_launcher_background)
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
            binding.tvDetailNamaBarang.text = penemuan.namaBarang
            binding.tvDetailKategori.text = penemuan.kategori
            binding.tvDetailDeskripsi.text = penemuan.deskripsi
            binding.tvDetailTanggal.text = penemuan.tanggalPenemuan
            binding.tvDetailWaktu.text = penemuan.waktuPenemuan
            binding.tvDetailLokasi.text = penemuan.lokasiPenemuan

            setupToggleButton(binding, penemuan)
        }

        private fun setupToggleButton(binding: ItemPenemuanBinding, penemuan: PenemuanModel) {
            binding.btnKiri.text = "Detail Barang"

            // --- 3. LOGIKA KONDISIONAL UNTUK TOMBOL ---
            if (isMyPostsPage) {
                // Jika ini adalah halaman "Postingan Saya"
                binding.btnKanan.text = "Verifikasi"
                binding.btnKanan.setOnClickListener {
                    penemuan.id?.let { postId ->
                        listener.onVerifikasiClick(postId)
                    }
                }
            } else {
                // Jika ini adalah halaman Home (Beranda)
                binding.btnKanan.text = "Klaim Barang"
                binding.btnKanan.setOnClickListener {
                    penemuan.id?.let { postId ->
                        listener.onKlaimClick(postId)
                    }
                }
            }
            // --- SELESAI PERUBAHAN LOGIKA TOMBOL ---

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

    fun updateData(newList: List<PenemuanModel>) {
        listPenemuan.clear()
        listPenemuan.addAll(newList)
        notifyDataSetChanged()
    }
}
