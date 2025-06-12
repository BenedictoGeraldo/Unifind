package com.androidprojek.unifind.ui.profile.lacakformulir

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.databinding.FragmentProfileLacakPenemuanListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ProfileLacakPenemuanListFragment : Fragment() {

    private var _binding: FragmentProfileLacakPenemuanListBinding? = null
    private val binding get() = _binding!!

    // TODO: Ganti dengan adapter dan model yang sesuai untuk melacak formulir
    // private lateinit var lacakAdapter: LacakFormulirAdapter
    // private val listLacak = mutableListOf<LacakModel>()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileLacakPenemuanListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setupRecyclerView()
        listenToMyActiveClaims()
    }

    private fun setupRecyclerView() {
        // lacakAdapter = LacakFormulirAdapter(listLacak)
        // binding.rvLacakPenemuan.apply {
        //     layoutManager = LinearLayoutManager(context)
        //     adapter = lacakAdapter
        // }
    }

    private fun listenToMyActiveClaims() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid == null) {
            binding.tvEmptyLacak.visibility = View.VISIBLE
            return
        }

        binding.progressBarLacak.visibility = View.VISIBLE
        binding.tvEmptyLacak.visibility = View.GONE

        // Query ke semua koleksi "klaim_barang" di seluruh aplikasi
        firestoreListener = db.collectionGroup("klaim_barang")
            // Filter 1: Ambil klaim yang dibuat oleh pengguna saat ini
            .whereEqualTo("uidPengklaim", currentUserUid)
            // Filter 2: Ambil klaim yang statusnya masih aktif
            .whereEqualTo("statusKlaim", "Menunggu Konfirmasi")
            .addSnapshotListener { snapshots, error ->
                if (_binding == null) return@addSnapshotListener
                binding.progressBarLacak.visibility = View.GONE

                if (error != null) {
                    Log.w("LacakFragment", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    // TODO: Isi data ke dalam list dan update adapter
                    // listLacak.clear()
                    // for (doc in snapshots.documents) { ... }
                    // lacakAdapter.notifyDataSetChanged()
                    binding.tvEmptyLacak.visibility = if (snapshots.isEmpty) View.VISIBLE else View.GONE
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firestoreListener?.remove()
        _binding = null
    }
}
