package com.androidprojek.unifind.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androidprojek.unifind.databinding.FragmentProfileMyPostsBinding
import com.google.android.material.tabs.TabLayoutMediator

class ProfileMyPostsFragment : Fragment() {

    private var _binding: FragmentProfileMyPostsBinding? = null
    private val binding get() = _binding!!

    private val tabTitles = arrayOf("Pencarian", "Penemuan")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup ViewPager dengan PagerAdapter yang baru
        val adapter = ProfilePostsPagerAdapter(this)
        binding.viewPagerMyPosts.adapter = adapter

        // Hubungkan TabLayout dengan ViewPager
        TabLayoutMediator(binding.tabLayoutMyPosts, binding.viewPagerMyPosts) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // Atur fungsi untuk tombol kembali di toolbar
        binding.toolbarMyPosts.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}