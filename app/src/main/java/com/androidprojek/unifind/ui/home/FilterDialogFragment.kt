package com.androidprojek.unifind.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.androidprojek.unifind.R
import com.androidprojek.unifind.model.KategoriModel
import com.androidprojek.unifind.databinding.FilterHomeBinding
import com.google.android.material.chip.Chip // Import Chip

class FilterDialogFragment : DialogFragment() {

    private var _binding: FilterHomeBinding? = null
    private val binding get() = _binding!!

    private val listKategori = mutableListOf(
        KategoriModel("Laptop"), KategoriModel("Handphone"), KategoriModel("Charger"),
        KategoriModel("Earphone"), KategoriModel("Jam Tangan"), KategoriModel("Alat Tulis"),
        KategoriModel("Jaket/Hoodie"), KategoriModel("Helm"), KategoriModel("Kartu Identitas"),
        KategoriModel("Kacamata")
    )
    private val selectedKategori = mutableListOf<KategoriModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilterHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Panggil fungsi baru untuk mengisi kategori
        populateCategoryChips()

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnTerapkanFilter.setOnClickListener {
            val hasilFilter = ArrayList(selectedKategori.map { it.nama })
            val bundle = Bundle()
            bundle.putStringArrayList("kategori_terpilih", hasilFilter)
            parentFragmentManager.setFragmentResult("filter_request", bundle)
            dismiss()
        }

        binding.btnHapusFilter.setOnClickListener {
            selectedKategori.clear()
            listKategori.forEach { it.isSelected = false }
            // Perbarui tampilan semua chip menjadi tidak terpilih
            updateChipStates()
        }
    }

    // FUNGSI BARU: Mengisi FlexboxLayout dengan Chip
    private fun populateCategoryChips() {
        val context = requireContext()
        val flexboxLayout = binding.flexboxKategori // Menggunakan ID FlexboxLayout

        // Hapus view lama jika ada (untuk mencegah duplikasi)
        flexboxLayout.removeAllViews()

        for (kategori in listKategori) {
            // Inflate layout chip dari item_kategori_filter.xml
            val chip = layoutInflater.inflate(R.layout.item_kategori_filter, flexboxLayout, false) as Chip
            chip.text = kategori.nama
            chip.isChecked = kategori.isSelected

            chip.setOnClickListener {
                handleCategorySelection(kategori)
                // Update state visual chip secara langsung
                (it as Chip).isChecked = kategori.isSelected
            }
            flexboxLayout.addView(chip)
        }
    }

    // FUNGSI BARU: Mengupdate state semua chip (untuk tombol Hapus)
    private fun updateChipStates() {
        val flexboxLayout = binding.flexboxKategori
        for (i in 0 until flexboxLayout.childCount) {
            val chip = flexboxLayout.getChildAt(i) as Chip
            chip.isChecked = false
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
                // Batalkan check pada chip yang gagal dipilih
                (binding.flexboxKategori.findViewWithTag(kategori.nama) as? Chip)?.isChecked = false
                Toast.makeText(context, "Maksimal 3 kategori", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}