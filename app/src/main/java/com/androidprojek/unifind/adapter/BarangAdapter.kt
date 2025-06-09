package com.androidprojek.unifind.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.androidprojek.unifind.R
import com.androidprojek.unifind.model.BarangModel
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import de.hdodenhof.circleimageview.CircleImageView

class BarangAdapter(private val listBarang: List<BarangModel>) :
    RecyclerView.Adapter<BarangAdapter.BarangViewHolder>() {

    inner class BarangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Header
        val ivFotoProfil: CircleImageView = itemView.findViewById(R.id.ivFotoProfil)
        val tvNamaPelapor: TextView = itemView.findViewById(R.id.tvNamaPelapor)
        val tvNimPelapor: TextView = itemView.findViewById(R.id.tvNimPelapor)
        val ivToggleDetail: ImageView = itemView.findViewById(R.id.ivToggleDetail)

        // Gambar
        val viewPagerGambarBarang: ViewPager2 = itemView.findViewById(R.id.viewPagerGambarBarang)
        val dotsIndicator: DotsIndicator = itemView.findViewById(R.id.dotsIndicator)

        // Info Utama
        val tvNamaBarang: TextView = itemView.findViewById(R.id.tvNamaBarang)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        // ==== TAMBAHKAN REFERENSI UNTUK SEMUA TEXTVIEW DETAIL DI SINI ====
        val layoutDetail: LinearLayout = itemView.findViewById(R.id.layoutDetail)
        val tvDetailKategori: TextView = itemView.findViewById(R.id.tvDetailKategori)
        val tvDetailDeskripsi: TextView = itemView.findViewById(R.id.tvDetailDeskripsi)
        val tvDetailTanggal: TextView = itemView.findViewById(R.id.tvDetailTanggal)
        val tvDetailWaktu: TextView = itemView.findViewById(R.id.tvDetailWaktu)
        val tvDetailLokasi: TextView = itemView.findViewById(R.id.tvDetailLokasi)

        // Tombol
        val btnDetail: MaterialButton = itemView.findViewById(R.id.btnDetail)
        val btnAksiUtama: MaterialButton = itemView.findViewById(R.id.btnAksiUtama)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barang, parent, false)
        return BarangViewHolder(view)
    }

    override fun onBindViewHolder(holder: BarangViewHolder, position: Int) {
        val barang = listBarang[position]

        // 1. Set data header
        holder.tvNamaPelapor.text = barang.nama
        holder.tvNimPelapor.text = barang.nim
        // Glide.with(holder.itemView.context).load(URL_FOTO_PROFIL_PELAPOR).into(holder.ivFotoProfil)

        // 2. Set data info utama
        holder.tvNamaBarang.text = barang.namaBarang
        holder.tvStatus.text = barang.status

        // ==== TAMBAHKAN KODE UNTUK MENGISI DATA DETAIL DI SINI ====
        // Data ini diisi sekali saja, tidak perlu di dalam OnClickListener,
        // agar siap ditampilkan kapan saja.
        holder.tvDetailKategori.text = barang.kategori
        holder.tvDetailDeskripsi.text = barang.deskripsi
        holder.tvDetailTanggal.text = barang.tanggalHilang
        holder.tvDetailWaktu.text = barang.waktuHilang
        holder.tvDetailLokasi.text = barang.lokasiHilang

        // 3. Setup Image Slider
        if (barang.fotoUris.isNotEmpty()) {
            holder.viewPagerGambarBarang.adapter = ImageSliderAdapter(barang.fotoUris)
            holder.dotsIndicator.attachTo(holder.viewPagerGambarBarang)
            holder.viewPagerGambarBarang.visibility = View.VISIBLE
            holder.dotsIndicator.visibility = View.VISIBLE
        } else {
            holder.viewPagerGambarBarang.visibility = View.GONE
            holder.dotsIndicator.visibility = View.GONE
        }

        // 4. Implementasi expand/collapse detail (Logika ini tetap sama)
        // Logika ini hanya mengatur visibilitas, datanya sudah kita isi di atas.
        holder.ivToggleDetail.setOnClickListener {
            val isVisible = holder.layoutDetail.visibility == View.VISIBLE
            if (isVisible) {
                holder.layoutDetail.visibility = View.GONE
                holder.ivToggleDetail.animate().rotation(0f).setDuration(200).start()
            } else {
                holder.layoutDetail.visibility = View.VISIBLE
                holder.ivToggleDetail.animate().rotation(180f).setDuration(200).start()
            }
        }

        // 5. Set listener untuk tombol
        holder.btnDetail.setOnClickListener {
            // Logika untuk menghubungi pelapor
        }

        holder.btnAksiUtama.setOnClickListener {
            // Logika untuk aksi "Saya Menemukan Ini"
        }
    }

    override fun getItemCount(): Int = listBarang.size
}