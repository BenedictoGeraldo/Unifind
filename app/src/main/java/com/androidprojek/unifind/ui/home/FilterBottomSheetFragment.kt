package com.androidprojek.unifind.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.androidprojek.unifind.adapter.KategoriFilterAdapter
import com.androidprojek.unifind.model.KategoriModel
import com.androidprojek.unifind.databinding.FragmentFilterBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val listKategori = mutableListOf(
        KategoriModel("Laptop"), KategoriModel("Handphone"), KategoriModel("Charger"),
        KategoriModel("Earphone"), KategoriModel("Jam Tangan"), KategoriModel("Alat Tulis"),
        KategoriModel("Jaket/Hoodie"), KategoriModel("Helm"), KategoriModel("Kartu Identitas"),
        KategoriModel("Kacamata")
    )
    private val selectedKategori = mutableListOf<KategoriModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = KategoriFilterAdapter(listKategori) { kategori ->
            handleCategorySelection(kategori)
        }

        binding.rvKategori.layoutManager = GridLayoutManager(context, 3)
        binding.rvKategori.adapter = adapter

        // Listener untuk tombol Terapkan Filter (tidak berubah)
        binding.btnTerapkanFilter.setOnClickListener {
            val hasilFilter = ArrayList(selectedKategori.map { it.nama })
            val bundle = Bundle()
            bundle.putStringArrayList("kategori_terpilih", hasilFilter)
            parentFragmentManager.setFragmentResult("filter_request", bundle)
            dismiss()
        }

        // --- TAMBAHKAN LOGIKA UNTUK TOMBOL HAPUS FILTER ---
        binding.btnHapusFilter.setOnClickListener {
            // 1. Kosongkan daftar kategori yang sudah dipilih
            selectedKategori.clear()

            // 2. Set semua item di daftar utama menjadi tidak terpilih
            listKategori.forEach { it.isSelected = false }

            // 3. Perbarui tampilan chip di RecyclerView
            binding.rvKategori.adapter?.notifyDataSetChanged()
        }
    }

    private fun handleCategorySelection(kategori: KategoriModel) {
        if (kategori.isSelected) {
            // Jika sudah dipilih, batalkan pilihan
            kategori.isSelected = false
            selectedKategori.remove(kategori)
        } else {
            // Jika belum dipilih, tambahkan ke pilihan
            if (selectedKategori.size < 3) {
                kategori.isSelected = true
                selectedKategori.add(kategori)
            } else {
                Toast.makeText(context, "Maksimal 3 kategori", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvKategori.adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}