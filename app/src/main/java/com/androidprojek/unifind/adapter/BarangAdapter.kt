package com.androidprojek.unifind.adapter

import android.content.Intent
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
import com.androidprojek.unifind.ui.KontakPelaporActivity
import com.androidprojek.unifind.ui.LaporPenemuanActivity
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

        // Detail Tersembunyi
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

        // --- PERUBAHAN UTAMA DI SINI ---
        // Logika Baru untuk Foto Profil
        if (barang.pelaporPhotoUrl.isNotEmpty()) {
            // Jika ada URL foto profil, muat dari URL tersebut
            Glide.with(holder.itemView.context)
                .load(barang.pelaporPhotoUrl)
                .placeholder(R.drawable.baseline_person_outline_24) // Opsional: gambar saat loading
                .error(R.drawable.baseline_person_outline_24) // Tampil jika URL error/kosong
                .into(holder.ivFotoProfil)
        } else {
            // Jika tidak ada URL, tampilkan gambar default secara eksplisit
            holder.ivFotoProfil.setImageResource(R.drawable.baseline_person_outline_24)
        }
        // ---------------------------------

        // 2. Set data info utama
        holder.tvNamaBarang.text = barang.namaBarang
        holder.tvStatus.text = barang.status

        // 3. Set data untuk detail tersembunyi
        holder.tvDetailKategori.text = barang.kategori
        holder.tvDetailDeskripsi.text = barang.deskripsi
        holder.tvDetailTanggal.text = barang.tanggalHilang
        holder.tvDetailWaktu.text = barang.waktuHilang
        holder.tvDetailLokasi.text = barang.lokasiHilang

        // 4. Setup Image Slider
        if (barang.fotoUris.isNotEmpty()) {
            holder.viewPagerGambarBarang.adapter = ImageSliderAdapter(barang.fotoUris)
            holder.dotsIndicator.attachTo(holder.viewPagerGambarBarang)
            holder.viewPagerGambarBarang.visibility = View.VISIBLE
            holder.dotsIndicator.visibility = View.VISIBLE
        } else {
            holder.viewPagerGambarBarang.visibility = View.GONE
            holder.dotsIndicator.visibility = View.GONE
        }

        // 5. Implementasi expand/collapse detail
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

        // 6. Set listener untuk tombol
        holder.btnDetail.setOnClickListener {
            // === PERUBAHAN DI SINI ===
            val context = holder.itemView.context
            // Buat intent untuk membuka KontakPelaporActivity
            val intent = Intent(context, KontakPelaporActivity::class.java).apply {
                // Kirim data kontak pelapor ke activity baru
                putExtra(KontakPelaporActivity.EXTRA_INSTAGRAM, barang.pelaporInstagram)
                putExtra(KontakPelaporActivity.EXTRA_LINE, barang.pelaporLine)
                putExtra(KontakPelaporActivity.EXTRA_WHATSAPP, barang.pelaporWhatsapp)
            }
            context.startActivity(intent)
            // =========================
        }
        holder.btnAksiUtama.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, LaporPenemuanActivity::class.java).apply {
                // --- PERUBAHAN DI SINI: KIRIM NAMA PELAPOR JUGA ---
                putExtra(LaporPenemuanActivity.EXTRA_BARANG_ID, barang.id)
                putExtra(LaporPenemuanActivity.EXTRA_PELAPOR_UID, barang.pelaporUid)
                putExtra(LaporPenemuanActivity.EXTRA_NAMA_BARANG, barang.namaBarang)
                putExtra(LaporPenemuanActivity.EXTRA_KATEGORI, barang.kategori)
                putExtra(LaporPenemuanActivity.EXTRA_NAMA_PELAPOR, barang.nama) // <-- KIRIM NAMA
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listBarang.size
}