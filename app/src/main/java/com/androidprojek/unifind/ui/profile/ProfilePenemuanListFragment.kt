package com.androidprojek.unifind.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.R
import com.androidprojek.unifind.adapter.OnItemClickListener
import com.androidprojek.unifind.adapter.PenemuanAdapter
import com.androidprojek.unifind.databinding.FragmentProfilePenemuanListBinding
import com.androidprojek.unifind.model.PenemuanModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProfilePenemuanListFragment : Fragment(), OnItemClickListener {

    private var _binding: FragmentProfilePenemuanListBinding? = null
    private val binding get() = _binding!!

    private lateinit var penemuanAdapter: PenemuanAdapter
    private val listPenemuan = mutableListOf<PenemuanModel>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePenemuanListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        listenToMyPosts()
    }

    private fun setupRecyclerView() {
        penemuanAdapter = PenemuanAdapter(listPenemuan, this, true)
        binding.rvMyPenemuan.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = penemuanAdapter
        }
    }

    private fun listenToMyPosts() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid == null) {
            binding.tvEmptyMyPenemuan.visibility = View.VISIBLE
            return
        }

        db.collection("form_penemuan")
            .whereEqualTo("uid", currentUserUid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("ProfilePenemuanList", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    listPenemuan.clear()
                    for (doc in snapshots.documents) {
                        val penemuan = doc.toObject(PenemuanModel::class.java)
                        if (penemuan != null) {
                            penemuan.id = doc.id
                            listPenemuan.add(penemuan)
                        }
                    }
                    penemuanAdapter.notifyDataSetChanged()
                    binding.tvEmptyMyPenemuan.visibility = if (listPenemuan.isEmpty()) View.VISIBLE else View.GONE
                }
            }
    }

    // Fungsi ini tidak akan pernah terpanggil di halaman ini,
    // tapi tetap harus ada untuk memenuhi kontrak interface.
    override fun onKlaimClick(postId: String) {
        // Biarkan kosong
    }

    // --- PERUBAHAN UTAMA DI SINI ---
    // Fungsi ini yang akan berjalan saat tombol "Verifikasi" diklik.
    override fun onVerifikasiClick(postId: String) {
        // 1. Buat Bundle untuk menampung data postId
        val bundle = Bundle().apply {
            putString("postId", postId)
        }

        // 2. Navigasi menggunakan ID dari action dan bundle yang sudah dibuat
        try {
            findNavController().navigate(R.id.action_profileMyPostsFragment_to_penemuanVerifikasiPemilikFragment, bundle)
        } catch (e: Exception) {
            Toast.makeText(context, "Navigasi gagal: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
