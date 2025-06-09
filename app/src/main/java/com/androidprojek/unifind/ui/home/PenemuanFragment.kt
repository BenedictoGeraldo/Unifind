package com.androidprojek.unifind.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.R
import com.androidprojek.unifind.adapter.PenemuanAdapter
import com.androidprojek.unifind.databinding.FragmentPenemuanBinding
import com.androidprojek.unifind.model.PenemuanModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PenemuanFragment : Fragment() {

    private var _binding: FragmentPenemuanBinding? = null
    private val binding get() = _binding!!

    private lateinit var penemuanAdapter: PenemuanAdapter
    private val listPenemuan = mutableListOf<PenemuanModel>()
    private val originalList = mutableListOf<PenemuanModel>()

    // Variabel untuk menyimpan filter kategori yang aktif
    private var activeCategoryFilters = listOf<String>()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPenemuanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        listenToFirestoreChanges()
        setupSearch()

        // Panggil fungsi untuk mendengarkan hasil filter
        setupFilterResultListener()

        binding.fabTambah.setOnClickListener {
            findNavController().navigate(R.id.action_penemuan_to_penemuanPostForm)
        }
    }

    private fun setupRecyclerView() {
        penemuanAdapter = PenemuanAdapter(listPenemuan)
        binding.rvBarang.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = penemuanAdapter
        }
    }

    private fun listenToFirestoreChanges() {
        db.collection("form_penemuan")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("PenemuanFragment", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    originalList.clear()
                    for (doc in snapshots.documents) {
                        val penemuan = doc.toObject(PenemuanModel::class.java)
                        if (penemuan != null) {
                            originalList.add(penemuan)
                        }
                    }
                    val currentQuery = (parentFragment as? HomeFragment)?.getSearchEditText()?.text.toString()
                    filterData(currentQuery)
                }
            }
    }

    private fun setupSearch() {
        (parentFragment as? HomeFragment)?.getSearchEditText()?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterData(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Fungsi baru untuk mendengarkan hasil dari BottomSheet
    private fun setupFilterResultListener() {
        parentFragmentManager.setFragmentResultListener("filter_request", this) { _, bundle ->
            val hasilFilter = bundle.getStringArrayList("kategori_terpilih")
            if (hasilFilter != null) {
                activeCategoryFilters = hasilFilter
                val currentQuery = (parentFragment as? HomeFragment)?.getSearchEditText()?.text.toString()
                filterData(currentQuery)
            }
        }
    }

    // Fungsi filterData yang sudah dimodifikasi
    private fun filterData(query: String) {
        val filteredList = mutableListOf<PenemuanModel>()
        val searchQuery = query.lowercase().trim()

        for (item in originalList) {
            // Cek kondisi pencarian teks
            val cocokPencarian = if (searchQuery.isEmpty()) {
                true
            } else {
                val namaBarang = item.namaBarang?.lowercase() ?: ""
                val kategoriItem = item.kategori?.lowercase() ?: ""
                namaBarang.contains(searchQuery) || kategoriItem.contains(searchQuery)
            }

            // Cek kondisi filter kategori
            val cocokFilter = if (activeCategoryFilters.isEmpty()) {
                true
            } else {
                // Cek apakah kategori item ada di dalam daftar filter yang aktif
                activeCategoryFilters.contains(item.kategori)
            }

            // Item akan ditampilkan jika cocok dengan KEDUA kondisi (pencarian DAN filter)
            if (cocokPencarian && cocokFilter) {
                filteredList.add(item)
            }
        }

        // Update adapter dengan data yang sudah difilter
        listPenemuan.clear()
        listPenemuan.addAll(filteredList)
        penemuanAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}