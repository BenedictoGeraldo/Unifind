package com.androidprojek.unifind.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.androidprojek.unifind.databinding.FragmentHomeBinding
import com.androidprojek.unifind.ui.home.adapter.HomePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    internal val binding get() = _binding!!

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

        val adapter = HomePagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // --- PERUBAHAN UTAMA ADA DI SINI ---
        binding.btnFilter.setOnClickListener {
            // Mengganti FilterBottomSheetFragment menjadi FilterDialogFragment
            val filterDialog = FilterDialogFragment()
            filterDialog.show(childFragmentManager, "FilterDialog")
        }
    }

    fun getSearchEditText(): EditText {
        return binding.etSearch
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}