package com.androidprojek.unifind.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.adapter.PenemuanAdapter
import com.androidprojek.unifind.databinding.FragmentProfilePenemuanListBinding
import com.androidprojek.unifind.model.PenemuanModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProfilePenemuanListFragment : Fragment() {

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
        penemuanAdapter = PenemuanAdapter(listPenemuan)
        binding.rvMyPenemuan.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = penemuanAdapter
        }
    }

    private fun listenToMyPosts() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid == null) {
            Log.w("ProfilePenemuanList", "User tidak login, tidak bisa mengambil data.")
            binding.tvEmptyMyPenemuan.visibility = View.VISIBLE
            return
        }

        db.collection("form_penemuan")
            .whereEqualTo("uid", currentUserUid) // INI BAGIAN PENTING: Filter berdasarkan UID
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
                            listPenemuan.add(penemuan)
                        }
                    }
                    penemuanAdapter.notifyDataSetChanged()

                    // Tampilkan pesan jika daftar kosong, sembunyikan jika ada isinya
                    binding.tvEmptyMyPenemuan.visibility = if (listPenemuan.isEmpty()) View.VISIBLE else View.GONE
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}