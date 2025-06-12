package com.androidprojek.unifind.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class PencarianFragment : Fragment() {

    private var _binding: FragmentPencarianBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val listBarang = mutableListOf<BarangModel>()
    private lateinit var barangAdapter: BarangAdapter

    private var firestoreListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPencarianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupRecyclerView()

        binding.fabTambah.setOnClickListener {
            startActivity(Intent(requireContext(), FormBarangActivity::class.java))
        }

        // Panggil listener saat view sudah dibuat
        listenToLostItems()
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
        if (currentUser == null) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvEmpty.text = "Silakan login untuk melihat postingan."
            return
        }
        val uidPenggunaSaatIni = currentUser.uid

        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        // --- PERUBAHAN UTAMA PADA QUERY ---
        // 1. Kita HANYA memfilter berdasarkan status yang masih aktif.
        //    Filter `whereNotEqualTo` kita pindahkan ke sisi aplikasi.
        val query = db.collection("barangHilang")
            .whereEqualTo("status", "Dalam Pencarian")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        firestoreListener = query.addSnapshotListener { snapshots, error ->
            if (_binding == null) {
                return@addSnapshotListener
            }
            binding.progressBar.visibility = View.GONE

            if (error != null) {
                Log.w("PencarianFragment", "Error listening for documents.", error)
                return@addSnapshotListener
            }

            if (snapshots != null) {
                listBarang.clear()

                // --- PERUBAHAN LOGIKA PENYARINGAN & PENGAMBILAN DATA ---
                // Loop manual untuk mendapatkan ID dan melakukan filter tambahan
                for (doc in snapshots.documents) {
                    val barang = doc.toObject(BarangModel::class.java)
                    if (barang != null) {
                        // Cek jika UID pelapor BUKAN pengguna saat ini
                        if (barang.pelaporUid != uidPenggunaSaatIni) {
                            // Ambil ID dokumen dan masukkan ke dalam model
                            barang.id = doc.id
                            // Tambahkan ke list hanya jika bukan milik sendiri
                            listBarang.add(barang)
                        }
                    }
                }
                // --- AKHIR PERUBAHAN ---

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
        // Hentikan listener untuk mencegah memory leak
        firestoreListener?.remove()
        _binding = null
    }
}