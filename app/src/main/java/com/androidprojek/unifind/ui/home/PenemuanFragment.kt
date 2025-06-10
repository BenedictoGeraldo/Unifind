package com.androidprojek.unifind.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels // --- 1. IMPORT BARU ---
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.R
import com.androidprojek.unifind.adapter.PenemuanAdapter
import com.androidprojek.unifind.databinding.FragmentPenemuanBinding

class PenemuanFragment : Fragment() {

    private var _binding: FragmentPenemuanBinding? = null
    private val binding get() = _binding!!

    // --- 2. INISIALISASI VIEWMODEL ---
    // Menggunakan instance ViewModel yang sama persis dengan HomeFragment
    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var penemuanAdapter: PenemuanAdapter

    // --- 3. HAPUS SEMUA PROPERTI LAMA ---
    // private val listPenemuan = mutableListOf<PenemuanModel>() -> Tidak perlu
    // private val originalList = mutableListOf<PenemuanModel>() -> Tidak perlu
    // private var activeCategoryFilters = listOf<String>() -> Tidak perlu
    // private val db = FirebaseFirestore.getInstance() -> Tidak perlu

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPenemuanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 4. onViewCreated MENJADI JAUH LEBIH SEDERHANA ---
        setupRecyclerView()
        observeViewModel() // Fungsi paling penting!
        setupFilterResultListener() // Fungsi ini juga menjadi lebih simpel

        binding.fabTambah.setOnClickListener {
            findNavController().navigate(R.id.action_penemuan_to_penemuanPostForm)
        }
    }

    private fun setupRecyclerView() {
        // Inisialisasi adapter dengan list kosong. Data akan di-supply oleh ViewModel.
        penemuanAdapter = PenemuanAdapter(mutableListOf())
        binding.rvBarang.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = penemuanAdapter
        }
    }

    // --- 5. FUNGSI BARU UNTUK MENGAMATI VIEWMODEL ---
    private fun observeViewModel() {
        // Amati filteredPenemuanList dari ViewModel.
        // Blok kode ini akan otomatis berjalan setiap kali ada perubahan data
        // (baik dari pencarian, filter, atau update dari Firestore).
        // `viewLifecycleOwner` memastikan observer ini hanya aktif saat fragment terlihat.
        homeViewModel.filteredPenemuanList.observe(viewLifecycleOwner) { penemuanList ->
            // Cukup berikan data yang sudah jadi ke adapter.
            // Fragment tidak perlu pusing memikirkan logika filter.
            penemuanAdapter.updateData(penemuanList)
        }
    }

    // --- 6. FUNGSI LISTENER YANG JADI LEBIH SIMPEL ---
    private fun setupFilterResultListener() {
        parentFragmentManager.setFragmentResultListener("filter_request", this) { _, bundle ->
            val hasilFilter = bundle.getStringArrayList("kategori_terpilih")
            // Bukan lagi memfilter manual, tapi hanya melapor ke ViewModel.
            homeViewModel.setActiveCategories(hasilFilter ?: emptyList())
        }
    }

    // --- 7. HAPUS SEMUA FUNGSI LAMA YANG TIDAK DIPERLUKAN LAGI ---
    // - listenToFirestoreChanges()
    // - setupSearch()
    // - filterData()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}