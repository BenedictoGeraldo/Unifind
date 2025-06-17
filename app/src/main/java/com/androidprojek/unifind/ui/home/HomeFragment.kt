package com.androidprojek.unifind.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.androidprojek.unifind.R
import com.androidprojek.unifind.databinding.FragmentHomeBinding
import com.androidprojek.unifind.ui.home.adapter.HomePagerAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    internal val binding get() = _binding!!

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
        observeViewModel()

        binding.btnFilter.setOnClickListener {
            val filterDialog = FilterDialogFragment()
            filterDialog.show(childFragmentManager, "FilterDialog")
        }
    }

    private fun observeViewModel() {
        // Amati LiveData userProfileImageUrl
        homeViewModel.userProfileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            // Gunakan Glide untuk memuat gambar dari URL
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.baseline_person_outline_24) // Gambar default saat loading
                    .error(R.drawable.baseline_person_outline_24)     // Gambar default jika gagal
                    .circleCrop() // Membuat gambar menjadi bulat
                    .into(binding.ivProfile) // Masukkan ke ImageView Anda
            } else {
                // Jika URL kosong atau null, tampilkan gambar default
                binding.ivProfile.setImageResource(R.drawable.baseline_person_outline_24)
            }
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