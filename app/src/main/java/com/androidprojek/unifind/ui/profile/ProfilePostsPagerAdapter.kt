package com.androidprojek.unifind.ui.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfilePostsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // Jumlah total tab
    override fun getItemCount(): Int = 2

    // Menentukan fragment mana yang akan ditampilkan untuk setiap posisi tab
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfilePencarianListFragment() // Untuk tab "Pencarian"
            1 -> ProfilePenemuanListFragment()  // Untuk tab "Penemuan"
            else -> throw IllegalStateException("Posisi tab tidak valid: $position")
        }
    }
}