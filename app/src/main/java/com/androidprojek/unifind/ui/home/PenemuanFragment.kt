package com.androidprojek.unifind.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.adapter.BarangAdapter
import com.androidprojek.unifind.databinding.FragmentPenemuanBinding
import com.androidprojek.unifind.model.BarangModel
import com.androidprojek.unifind.ui.FormBarangActivity

class PenemuanFragment : Fragment() {

    private var _binding: FragmentPenemuanBinding? = null
    private val binding get() = _binding!!

    private val listBarang = mutableListOf<BarangModel>()
    private lateinit var adapter: BarangAdapter

    companion object {
        const val REQUEST_TAMBAH = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPenemuanBinding.inflate(inflater, container, false)

        adapter = BarangAdapter(listBarang)
        binding.rvBarang.layoutManager = LinearLayoutManager(context)
        binding.rvBarang.adapter = adapter

        binding.fabTambah.setOnClickListener {
            startActivityForResult(
                Intent(requireContext(), FormBarangActivity::class.java),
                REQUEST_TAMBAH
            )
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAMBAH && resultCode == Activity.RESULT_OK && data != null) {
            val barang = data.getParcelableExtra<BarangModel>("DATA_BARANG")
            barang?.let {
                listBarang.add(it)
                adapter.notifyItemInserted(listBarang.size - 1)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
