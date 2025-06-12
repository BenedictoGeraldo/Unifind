package com.androidprojek.unifind.ui.profile.verifikasi

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
import com.androidprojek.unifind.adapter.PenemuanPengklaimAdapter
import com.androidprojek.unifind.databinding.ProfilePenemuanVerifikasiPemilikBinding
import com.androidprojek.unifind.model.PenemuanKlaimModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

// --- HAPUS IMPLEMENTASI INTERFACE DARI SINI ---
class PenemuanVerifikasiPemilikFragment : Fragment() {

    private var _binding: ProfilePenemuanVerifikasiPemilikBinding? = null
    private val binding get() = _binding!!

    private var postId: String? = null

    private lateinit var pengklaimAdapter: PenemuanPengklaimAdapter
    private val listKlaim = mutableListOf<PenemuanKlaimModel>()

    private val db = FirebaseFirestore.getInstance()
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getString("postId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfilePenemuanVerifikasiPemilikBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        listenToClaims()

        binding.toolbarVerifikasi.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        // --- PERUBAHAN UTAMA SAAT MEMBUAT ADAPTER ---
        pengklaimAdapter = PenemuanPengklaimAdapter(listKlaim,
            // Lambda untuk "Lihat Jawaban"
            onLihatJawaban = { klaim ->
                navigateToDetailJawaban(klaim)
            },
            // Lambda untuk "Kontak"
            onKontak = { klaim ->
                Toast.makeText(requireContext(), "Kontak ${klaim.namaPengklaim}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvPengklaim.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pengklaimAdapter
        }
    }

    private fun listenToClaims() {
        if (postId == null) {
            Log.e("VerifikasiFragment", "Error: Post ID tidak ditemukan.")
            binding.tvEmptyVerifikasi.visibility = View.VISIBLE
            binding.tvEmptyVerifikasi.text = "Error: ID Postingan tidak valid."
            return
        }

        binding.progressBarVerifikasi.visibility = View.VISIBLE
        binding.tvEmptyVerifikasi.visibility = View.GONE

        val query = db.collection("form_penemuan").document(postId!!)
            .collection("klaim_barang")
            .orderBy("timestampKlaim", Query.Direction.ASCENDING)

        firestoreListener = query.addSnapshotListener { snapshots, error ->
            if (_binding == null) return@addSnapshotListener

            binding.progressBarVerifikasi.visibility = View.GONE

            if (error != null) {
                Log.w("VerifikasiFragment", "Listen failed.", error)
                return@addSnapshotListener
            }

            if (snapshots != null) {
                listKlaim.clear()
                for (doc in snapshots.documents) {
                    val klaim = doc.toObject(PenemuanKlaimModel::class.java)
                    if (klaim != null) {
                        klaim.id = doc.id
                        listKlaim.add(klaim)
                    }
                }
                pengklaimAdapter.notifyDataSetChanged()

                binding.tvEmptyVerifikasi.visibility = if (listKlaim.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun navigateToDetailJawaban(klaim: PenemuanKlaimModel) {
        val bundle = Bundle().apply {
            putParcelable("dataKlaim", klaim)
        }
        findNavController().navigate(R.id.action_verifikasiPemilik_to_detailJawaban, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firestoreListener?.remove()
        _binding = null
    }
}
