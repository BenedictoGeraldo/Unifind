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

class PenemuanAdapter(private val listPenemuan: List<PenemuanModel>) : RecyclerView.Adapter<PenemuanAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemPenemuanBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(penemuan: PenemuanModel) {
            // MENYESUAIKAN DENGAN PenemuanModel
            binding.tvNamaPenemu.text = penemuan.namaPelapor
            binding.tvNimPenemu.text = penemuan.nim
            binding.tvNamaBarang.text = penemuan.namaBarang

            // Karena 'status' tidak ada di model, kita atur secara statis sesuai desain
            binding.tvStatus.text = "Dalam Pencarian" //

            // ## Penyesuaian Foto Profil ##
            // PenemuanModel tidak punya URL foto profil. Kode Glide dinonaktifkan.
            // Anda bisa mengaktifkannya kembali jika field-nya sudah ditambahkan ke model.
            /*
            Glide.with(itemView.context)
                .load(penemuan.URL_FOTO_PROFIL_ANDA) // Ganti dengan field yang benar nanti
                .circleCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.ivFotoProfil)
            */
            // Untuk sementara, kita bisa set gambar default saja
            binding.ivFotoProfil.setImageResource(R.drawable.ic_launcher_background)


            // ## Penyesuaian Gambar Barang (ViewPager2) ##
            // Model hanya punya satu imageUrl, bukan list.
            penemuan.imageUrl?.let { url ->
                if (url.isNotEmpty()) {
                    // Buat list yang hanya berisi satu URL
                    val imageUrlList = listOf(url)
                    val imageAdapter = ImageSliderAdapter(imageUrlList)
                    binding.viewPagerGambarBarang.adapter = imageAdapter

                    // Sembunyikan dots indicator karena hanya ada satu gambar
                    binding.dotsIndicator.visibility = View.GONE
                    binding.viewPagerGambarBarang.visibility = View.VISIBLE
                } else {
                    // Sembunyikan jika tidak ada URL gambar
                    binding.viewPagerGambarBarang.visibility = View.GONE
                    binding.dotsIndicator.visibility = View.GONE
                }
            } ?: run {
                // Sembunyikan juga jika imageUrl null
                binding.viewPagerGambarBarang.visibility = View.GONE
                binding.dotsIndicator.visibility = View.GONE
            }

            // MENYESUAIKAN DENGAN PenemuanModel untuk bagian detail
            binding.tvDetailNamaBarang.text = penemuan.namaBarang
            binding.tvDetailKategori.text = penemuan.kategori
            binding.tvDetailDeskripsi.text = penemuan.deskripsi
            binding.tvDetailTanggal.text = penemuan.tanggalPenemuan
            binding.tvDetailWaktu.text = penemuan.waktuPenemuan
            binding.tvDetailLokasi.text = penemuan.lokasiPenemuan

            // Logika Toggle Button tidak perlu diubah
            setupToggleButton(binding)
        }

        private fun setupToggleButton(binding: ItemPenemuanBinding) {
            binding.btnKiri.text = "Detail Barang" //
            binding.btnKanan.text = "Klaim Barang" //

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
                // Logika untuk klaim barang
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