package com.androidprojek.unifind.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels // <-- UBAH IMPORT INI
import com.androidprojek.unifind.databinding.FragmentHomeBinding
import com.androidprojek.unifind.ui.home.adapter.HomePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    internal val binding get() = _binding!!

    // --- PERUBAHAN UTAMA DI SINI ---
    // Ganti 'viewModels()' menjadi 'activityViewModels()'.
    // Ini memastikan instance ViewModel ini SAMA PERSIS dengan yang digunakan
    // oleh PenemuanFragment, karena keduanya berbagi Activity yang sama.
    private val homeViewModel: HomeViewModel by activityViewModels()

    private val tabTitles = arrayOf("Pencarian", "Penemuan")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabs()
        setupSearchListener()

        binding.btnFilter.setOnClickListener {
            val filterDialog = FilterDialogFragment()
            filterDialog.show(childFragmentManager, "FilterDialog")
        }
    }

    private fun setupTabs() {
        val adapter = HomePagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Kirim query terbaru ke ViewModel bersama
                homeViewModel.setSearchQuery(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
