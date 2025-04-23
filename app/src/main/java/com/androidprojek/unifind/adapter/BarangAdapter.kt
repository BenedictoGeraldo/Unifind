package com.androidprojek.unifind.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidprojek.unifind.R
import com.androidprojek.unifind.model.BarangModel
import com.androidprojek.unifind.ui.DetailBarangActivity

class BarangAdapter(private val listBarang: List<BarangModel>) :
    RecyclerView.Adapter<BarangAdapter.BarangViewHolder>() {

    inner class BarangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBarang: ImageView = itemView.findViewById(R.id.ivBarang)
        val tvNamaBarang: TextView = itemView.findViewById(R.id.tvNamaBarang)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val tvLokasi: TextView = itemView.findViewById(R.id.tvLokasi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barang, parent, false)
        return BarangViewHolder(view)
    }

    override fun onBindViewHolder(holder: BarangViewHolder, position: Int) {
        val barang = listBarang[position]
        holder.tvNamaBarang.text = barang.namaBarang
        holder.tvTanggal.text = barang.tanggalHilang
        holder.tvLokasi.text = barang.lokasiHilang
        try {
            holder.ivBarang.setImageURI(Uri.parse(barang.fotoUri))
        } catch (e: Exception) {
            e.printStackTrace()
            holder.ivBarang.setImageResource(R.drawable.warning_vector) // fallback image
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailBarangActivity::class.java)
            intent.putExtra("DATA_BARANG", barang)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listBarang.size
}
