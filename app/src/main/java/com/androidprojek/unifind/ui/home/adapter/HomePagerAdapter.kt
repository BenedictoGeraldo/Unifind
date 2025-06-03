package com.androidprojek.unifind.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.androidprojek.unifind.ui.home.PenemuanFragment
import com.androidprojek.unifind.ui.home.PencarianFragment

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PencarianFragment()
            1 -> PenemuanFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}
