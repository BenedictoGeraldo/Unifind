package com.androidprojek.unifind.ui.profile.lacakformulir

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfileLacakPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // Jumlah total tab yang akan kita tampilkan
    override fun getItemCount(): Int = 2

    /**
     * Fungsi ini menentukan fragment mana yang akan ditampilkan untuk setiap posisi tab.
     * Posisi 0 adalah tab pertama (kiri), Posisi 1 adalah tab kedua (kanan), dan seterusnya.
     */
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            // Untuk tab "Pencarian" (posisi 0), kita tampilkan Fragment kosong untuk sementara.
            0 -> Fragment()

            // Untuk tab "Penemuan" (posisi 1), kita tampilkan fragment yang sudah kita buat.
            1 -> ProfileLacakPenemuanListFragment()

            // Pengaman jika ada posisi yang tidak valid.
            else -> throw IllegalStateException("Posisi tab tidak valid: $position")
        }
    }
}
