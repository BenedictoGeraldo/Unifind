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
import com.google.firebase.firestore.ListenerRegistration // <-- 1. TAMBAHKAN IMPORT INI
import com.google.firebase.firestore.Query

class PencarianFragment : Fragment() {

    private var _binding: FragmentPencarianBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val listBarang = mutableListOf<BarangModel>()
    private lateinit var barangAdapter: BarangAdapter

    // --- 2. DEKLARASIKAN VARIABEL UNTUK MENAMPUNG LISTENER ---
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPencarianBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Pindahkan logika ke onViewCreated untuk praktik terbaik
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupRecyclerView()

        binding.fabTambah.setOnClickListener {
            startActivity(Intent(requireContext(), FormBarangActivity::class.java))
        }

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

        val query = db.collection("barangHilang")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .whereNotEqualTo("pelaporUid", uidPenggunaSaatIni)

        // --- 3. SIMPAN LISTENER KE DALAM VARIABEL ---
        firestoreListener = query.addSnapshotListener { snapshots, error ->
            // Tambahan keamanan: Cek jika binding masih ada sebelum melakukan apa pun
            if (_binding == null) {
                return@addSnapshotListener
            }

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

        // --- 4. HENTIKAN LISTENER SAAT FRAGMENT DIHANCURKAN ---
        firestoreListener?.remove()

        _binding = null
    }
}