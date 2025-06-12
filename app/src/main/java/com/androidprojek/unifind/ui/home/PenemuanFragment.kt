package com.androidprojek.unifind.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.R
import com.androidprojek.unifind.adapter.OnItemClickListener
import com.androidprojek.unifind.adapter.PenemuanAdapter
import com.androidprojek.unifind.databinding.FragmentPenemuanBinding

class PenemuanFragment : Fragment(), OnItemClickListener {

    private var _binding: FragmentPenemuanBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var penemuanAdapter: PenemuanAdapter

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
        observeViewModel()
        setupFilterResultListener()

        binding.fabTambah.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_penemuan_post_form)
        }
    }

    private fun setupRecyclerView() {
        // Saat membuat adapter di Beranda, 'isMyPostsPage' tidak perlu diisi (defaultnya false)
        penemuanAdapter = PenemuanAdapter(mutableListOf(), this)
        binding.rvBarang.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = penemuanAdapter
        }
    }

    private fun observeViewModel() {
        homeViewModel.filteredPenemuanList.observe(viewLifecycleOwner) { penemuanList ->
            penemuanAdapter.updateData(penemuanList)
            // Anda bisa tambahkan logika untuk menampilkan pesan kosong di sini
            // binding.tvEmpty.visibility = if (penemuanList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupFilterResultListener() {
        parentFragmentManager.setFragmentResultListener("filter_request", this) { _, bundle ->
            val hasilFilter = bundle.getStringArrayList("kategori_terpilih")
            homeViewModel.setActiveCategories(hasilFilter ?: emptyList())
        }
    }

    override fun onKlaimClick(postId: String) {
        val bundle = Bundle().apply {
            putString("postId", postId)
        }
        findNavController().navigate(R.id.action_navigation_home_to_penemuanFormKlaimBarangFragment, bundle)
    }

    // --- TAMBAHKAN FUNGSI INI UNTUK MEMPERBAIKI ERROR ---
    override fun onVerifikasiClick(postId: String) {
        // Biarkan kosong. Tombol "Verifikasi" tidak akan pernah muncul di halaman Beranda,
        // jadi fungsi ini tidak akan pernah dipanggil di sini.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
