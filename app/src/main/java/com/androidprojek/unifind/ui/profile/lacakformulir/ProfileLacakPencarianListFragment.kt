package com.androidprojek.unifind.ui.profile.lacakformulir

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.adapter.PencarianLacakAdapter
import com.androidprojek.unifind.databinding.FragmentProfileLacakPencarianListBinding
import com.androidprojek.unifind.model.BarangModel
import com.androidprojek.unifind.model.LaporanPenemuanModel
import com.androidprojek.unifind.model.PencarianLacakFormulirModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileLacakPencarianListFragment : Fragment() {

    private var _binding: FragmentProfileLacakPencarianListBinding? = null
    private val binding get() = _binding!!

    private lateinit var lacakAdapter: PencarianLacakAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Ganti nama binding class sesuai nama file XML baru Anda jika berbeda
        _binding = FragmentProfileLacakPencarianListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        listenToMySubmittedReports()
    }

    private fun setupRecyclerView() {
        lacakAdapter = PencarianLacakAdapter(emptyList())
        // Ganti ID RecyclerView sesuai nama file XML baru Anda jika berbeda
        binding.rvLacakPencarian.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = lacakAdapter
        }
    }

    private fun listenToMySubmittedReports() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid == null) {
            binding.tvEmptyLacak.visibility = View.VISIBLE
            return
        }

        binding.progressBarLacak.visibility = View.VISIBLE
        binding.tvEmptyLacak.visibility = View.GONE

        val query = db.collectionGroup("laporanPenemuan")
            .whereEqualTo("penemuUid", currentUserUid)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        firestoreListener = query.addSnapshotListener { snapshots, error ->
            if (_binding == null) return@addSnapshotListener
            binding.progressBarLacak.visibility = View.GONE

            if (error != null) {
                Log.w("LacakPencarian", "Listen failed.", error)
                return@addSnapshotListener
            }

            if (snapshots != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val lacakItems = snapshots.documents.mapNotNull { laporanDoc ->
                        val postinganRef = laporanDoc.reference.parent.parent
                        if (postinganRef != null) {
                            try {
                                val postinganDoc = postinganRef.get().await()
                                if (postinganDoc.exists()) {
                                    val postingan = postinganDoc.toObject(BarangModel::class.java)
                                    val laporan = laporanDoc.toObject(LaporanPenemuanModel::class.java)

                                    if (postingan != null && laporan != null) {
                                        // --- PERUBAHAN DI SINI ---
                                        PencarianLacakFormulirModel(
                                            namaBarang = postingan.namaBarang,
                                            namaPoster = postingan.nama,
                                            // Ambil URL gambar pertama, jika ada
                                            imageUrlPostingan = if (postingan.fotoUris.isNotEmpty()) postingan.fotoUris[0] else null,
                                            statusLaporan = laporan.statusLaporan
                                        )
                                    } else null
                                } else null
                            } catch (e: Exception) {
                                Log.e("LacakPencarian", "Gagal mengambil data postingan induk", e)
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