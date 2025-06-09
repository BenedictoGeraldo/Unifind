package com.androidprojek.unifind.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.adapter.BarangAdapter
import com.androidprojek.unifind.databinding.FragmentPencarianBinding
import com.androidprojek.unifind.model.BarangModel
import com.androidprojek.unifind.ui.FormBarangActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PencarianFragment : Fragment() {

    private var _binding: FragmentPencarianBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi Firebase Firestore
    private lateinit var db: FirebaseFirestore

    // List untuk menampung data dari Firestore
    private val listBarang = mutableListOf<BarangModel>()
    private lateinit var barangAdapter: BarangAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPencarianBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()

        // Navigasi ke FormBarangActivity, tidak lagi menggunakan onActivityResult
        binding.fabTambah.setOnClickListener {
            startActivity(Intent(requireContext(), FormBarangActivity::class.java))
        }

        // Mulai mendengarkan data dari Firestore
        listenToLostItems()

        return binding.root
    }

    private fun setupRecyclerView() {
        barangAdapter = BarangAdapter(listBarang)
        binding.rvBarang.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = barangAdapter
        }
    }

    private fun listenToLostItems() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        // Query ke koleksi "barangHilang", diurutkan berdasarkan timestamp terbaru
        db.collection("barangHilang")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                binding.progressBar.visibility = View.GONE

                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    // Kosongkan list lama sebelum diisi data baru
                    listBarang.clear()
                    // Ubah semua dokumen menjadi objek BarangModel
                    val result = snapshots.toObjects(BarangModel::class.java)
                    listBarang.addAll(result)
                    // Beri tahu adapter bahwa data telah berubah
                    barangAdapter.notifyDataSetChanged()
                }

                // Tampilkan pesan jika daftar kosong
                if (listBarang.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}