package com.androidprojek.unifind.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.adapter.PencarianPostinganSayaAdapter // Ganti ke adapter yang sesuai jika ada
import com.androidprojek.unifind.databinding.FragmentProfilePencarianListBinding
import com.androidprojek.unifind.model.BarangModel // Ganti ke model yang sesuai jika ada
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProfilePencarianListFragment : Fragment() {

    private var _binding: FragmentProfilePencarianListBinding? = null
    private val binding get() = _binding!!

    // Ganti ke adapter dan model yang sesuai untuk "Pencarian"
    private lateinit var pencarianAdapter: PencarianPostinganSayaAdapter
    private val listPencarian = mutableListOf<BarangModel>()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePencarianListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("FragmentLifecycle", "ProfilePencarianListFragment - onViewCreated DIPANGGIL!")

        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        listenToMyPosts()
    }

    private fun setupRecyclerView() {
        pencarianAdapter = PencarianPostinganSayaAdapter(listPencarian) // Gunakan adapter "Pencarian"
        binding.rvMyPencarian.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pencarianAdapter
        }

        // Implementasikan listener-nya
        pencarianAdapter.onLihatLaporanClickListener = { barang ->
            // --- PERUBAHAN DI SINI ---
            // Kita gunakan isNullOrEmpty() untuk memeriksa String?
            if (!barang.id.isNullOrEmpty()) {
                val intent = Intent(context, LaporanMasukActivity::class.java).apply {
                    putExtra(LaporanMasukActivity.EXTRA_BARANG_ID, barang.id) // Kirim ID barang
                }
                startActivity(intent)
            } else {
                Toast.makeText(context, "Gagal mendapatkan ID postingan.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun listenToMyPosts() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid == null) {
            binding.tvEmptyMyPencarian.visibility = View.VISIBLE
            return
        }

        // --- NAMA KOLEKSI DISESUAIKAN DI SINI ---
        db.collection("barangHilang") // Mengambil dari koleksi "barangHilang"
            .whereEqualTo("pelaporUid", currentUserUid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("ProfilePencarianList", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    listPencarian.clear()
                    // --- PERUBAHAN LOGIKA DI SINI ---
                    // Kita tidak langsung .toObjects(), tapi kita loop manual
                    for (doc in snapshots.documents) {
                        // 1. Ubah dokumen menjadi objek BarangModel
                        val barang = doc.toObject(BarangModel::class.java)
                        if (barang != null) {
                            // 2. Ambil ID dokumen dan masukkan ke field 'id' di model
                            barang.id = doc.id

                            // 3. Tambahkan objek yang sudah lengkap ke dalam list
                            listPencarian.add(barang)
                        }
                    }
                    // --- AKHIR PERUBAHAN ---
                    pencarianAdapter.notifyDataSetChanged()
                    binding.tvEmptyMyPencarian.visibility = if (listPencarian.isEmpty()) View.VISIBLE else View.GONE
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}