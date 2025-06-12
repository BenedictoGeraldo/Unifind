package com.androidprojek.unifind.ui.profile.lacakformulir

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androidprojek.unifind.databinding.FragmentProfileLacakFormulirBinding
import com.google.android.material.tabs.TabLayoutMediator

class ProfileLacakFormulirFragment : Fragment() {

    private var _binding: FragmentProfileLacakFormulirBinding? = null
    private val binding get() = _binding!!

    private val tabTitles = arrayOf("Pencarian", "Penemuan")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileLacakFormulirBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup ViewPager dengan PagerAdapter baru
        // TODO: Buat ProfileLacakPagerAdapter di langkah berikutnya
         val adapter = ProfileLacakPagerAdapter(this)
         binding.viewPagerLacakFormulir.adapter = adapter

        // Hubungkan TabLayout dengan ViewPager
        TabLayoutMediator(binding.tabLayoutLacakFormulir, binding.viewPagerLacakFormulir) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // Atur fungsi untuk tombol kembali di toolbar
        binding.toolbarLacakFormulir.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
