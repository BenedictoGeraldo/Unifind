package com.androidprojek.unifind.ui.profile.lacakformulir

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.adapter.LacakFormulirAdapter
import com.androidprojek.unifind.databinding.FragmentProfileLacakPenemuanListBinding
import com.androidprojek.unifind.model.PenemuanKlaimModel
import com.androidprojek.unifind.model.PenemuanLacakFormulirModel
import com.androidprojek.unifind.model.PenemuanModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileLacakPenemuanListFragment : Fragment() {

    private var _binding: FragmentProfileLacakPenemuanListBinding? = null
    private val binding get() = _binding!!

    private lateinit var lacakAdapter: LacakFormulirAdapter
    private val listLacak = mutableListOf<PenemuanLacakFormulirModel>()

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
        setupRecyclerView()
        listenToMyActiveClaims()
    }

    private fun setupRecyclerView() {
        lacakAdapter = LacakFormulirAdapter(listLacak)
        binding.rvLacakPenemuan.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = lacakAdapter
        }
    }

    private fun listenToMyActiveClaims() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid == null) {
            binding.tvEmptyLacak.visibility = View.VISIBLE
            return
        }

        binding.progressBarLacak.visibility = View.VISIBLE
        binding.tvEmptyLacak.visibility = View.GONE

        val query = db.collectionGroup("klaim_barang")
            .whereEqualTo("uidPengklaim", currentUserUid)
            .whereEqualTo("statusKlaim", "Menunggu Konfirmasi")

        firestoreListener = query.addSnapshotListener { snapshots, error ->
            if (_binding == null) return@addSnapshotListener
            binding.progressBarLacak.visibility = View.GONE

            if (error != null) {
                Log.w("LacakFragment", "Listen failed.", error)
                return@addSnapshotListener
            }

            if (snapshots != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val lacakItems = snapshots.documents.mapNotNull { claimDoc ->
                        val postinganRef = claimDoc.reference.parent.parent
                        if (postinganRef != null) {
                            try {
                                val postinganDoc = postinganRef.get().await()
                                if (postinganDoc.exists()) {
                                    val postingan = postinganDoc.toObject(PenemuanModel::class.java)
                                    val klaim = claimDoc.toObject(PenemuanKlaimModel::class.java)

                                    if (postingan != null && klaim != null) {
                                        PenemuanLacakFormulirModel(
                                            postId = postinganDoc.id,
                                            namaBarangPostingan = postingan.namaBarang,
                                            imageUrlPostingan = postingan.imageUrl,
                                            namaPenemu = postingan.namaPelapor,
                                            klaimId = claimDoc.id,
                                            statusKlaim = klaim.statusKlaim,
                                            detailKlaim = klaim
                                        )
                                    } else null
                                } else null
                            } catch (e: Exception) {
                                Log.e("LacakFragment", "Gagal mengambil data postingan induk", e)
                                null
                            }
                        } else null
                    }
                    lacakAdapter.updateData(lacakItems)
                    binding.tvEmptyLacak.visibility = if (lacakItems.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firestoreListener?.remove()
        _binding = null
    }
}