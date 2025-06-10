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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PencarianFragment : Fragment() {

    private var _binding: FragmentPencarianBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val listBarang = mutableListOf<BarangModel>()
    private lateinit var barangAdapter: BarangAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPencarianBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance() // Inisialisasi Auth

        setupRecyclerView()

        binding.fabTambah.setOnClickListener {
            startActivity(Intent(requireContext(), FormBarangActivity::class.java))
        }

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
        val currentUser = auth.currentUser
        // Jika tidak ada user yang login, jangan tampilkan apa-apa
        if (currentUser == null) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvEmpty.text = "Silakan login untuk melihat postingan."
            return
        }
        val uidPenggunaSaatIni = currentUser.uid

        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        // --- PERUBAHAN UTAMA DI SINI ---
        // Query ke Firestore dengan filter "TIDAK SAMA DENGAN" UID kita
        db.collection("barangHilang")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .whereNotEqualTo("pelaporUid", uidPenggunaSaatIni) // Filter postingan
            .addSnapshotListener { snapshots, error ->
                binding.progressBar.visibility = View.GONE

                if (error != null) { return@addSnapshotListener }

                if (snapshots != null) {
                    listBarang.clear()
                    val result = snapshots.toObjects(BarangModel::class.java)
                    listBarang.addAll(result)
                    barangAdapter.notifyDataSetChanged()
                }

                if (listBarang.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.text = "Belum ada laporan kehilangan dari pengguna lain."
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