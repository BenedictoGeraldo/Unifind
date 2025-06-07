package com.androidprojek.unifind.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.R
// DIUBAH: Mengimpor adapter baru Anda
import com.androidprojek.unifind.adapter.PenemuanAdapter
import com.androidprojek.unifind.databinding.FragmentPenemuanBinding
// DIUBAH: Mengimpor model baru Anda
import com.androidprojek.unifind.model.PenemuanModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PenemuanFragment : Fragment() {

    private var _binding: FragmentPenemuanBinding? = null
    private val binding get() = _binding!!

    // DIUBAH: Deklarasi variabel untuk adapter dan list baru Anda
    private lateinit var penemuanAdapter: PenemuanAdapter
    private val listPenemuan = mutableListOf<PenemuanModel>()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPenemuanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.fabTambah.setOnClickListener {
            // Navigasi ini sudah benar, mengarah ke form Anda
            findNavController().navigate(R.id.action_penemuan_to_penemuanPostForm)
        }

        // Fungsi ini akan mengambil data dari Firestore
        listenToFirestoreChanges()
    }

    private fun setupRecyclerView() {
        // DIUBAH: Menggunakan PenemuanAdapter yang baru
        penemuanAdapter = PenemuanAdapter(listPenemuan)
        binding.rvBarang.apply {
            layoutManager = LinearLayoutManager(context)
            // DIUBAH: Menetapkan adapter baru ke RecyclerView
            adapter = penemuanAdapter
        }
    }

    private fun listenToFirestoreChanges() {
        // Mengambil data dari koleksi "form_penemuan" dan mengurutkannya berdasarkan yang terbaru
        db.collection("form_penemuan")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("PenemuanFragment", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    // DIUBAH: Mengosongkan dan mengisi listPenemuan
                    listPenemuan.clear()
                    for (doc in snapshots.documents) {
                        // DIUBAH: Mengonversi data Firestore menjadi objek PenemuanModel
                        val penemuan = doc.toObject(PenemuanModel::class.java)
                        if (penemuan != null) {
                            listPenemuan.add(penemuan)
                        }
                    }
                    // DIUBAH: Memberi tahu penemuanAdapter bahwa datanya telah berubah
                    penemuanAdapter.notifyDataSetChanged()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
