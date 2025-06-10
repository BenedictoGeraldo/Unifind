package com.androidprojek.unifind.ui.home

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidprojek.unifind.model.PenemuanModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    // TAMBAHKAN: Instance FirebaseAuth untuk mendapatkan UID pengguna
    private val auth = FirebaseAuth.getInstance()

    // DATA SUMBER (INPUTS)
    private val _originalList = MutableLiveData<List<PenemuanModel>>()
    private val _searchQuery = MutableLiveData<String>("")
    private val _activeCategories = MutableLiveData<List<String>>(emptyList())

    // DATA HASIL (OUTPUT)
    val filteredPenemuanList = MediatorLiveData<List<PenemuanModel>>()

    init {
        listenToFirestoreChanges()

        filteredPenemuanList.addSource(_originalList) { applyFiltersAndSearch() }
        filteredPenemuanList.addSource(_searchQuery) { applyFiltersAndSearch() }
        filteredPenemuanList.addSource(_activeCategories) { applyFiltersAndSearch() }
    }

    /**
     * Mengambil data secara real-time dari koleksi 'form_penemuan',
     * MENGECUALIKAN postingan milik pengguna saat ini,
     * dan mengurutkannya berdasarkan timestamp terbaru.
     */
    private fun listenToFirestoreChanges() {
        val currentUserUid = auth.currentUser?.uid

        // Jika pengguna tidak login, jangan tampilkan apa-apa di Beranda.
        if (currentUserUid == null) {
            _originalList.value = emptyList()
            return
        }

        // --- PERUBAHAN UTAMA DI SINI ---
        db.collection("form_penemuan")
            // Hanya ambil dokumen yang 'uid'-nya TIDAK SAMA DENGAN uid pengguna saat ini
            .whereNotEqualTo("uid", currentUserUid)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("HomeViewModel", "Gagal mendengarkan data Firestore.", error)
                    return@addSnapshotListener
                }

                // Ubah dokumen snapshot menjadi daftar objek PenemuanModel
                val list = snapshots?.toObjects(PenemuanModel::class.java)
                _originalList.value = list ?: emptyList() // Update LiveData dengan data baru
            }
    }

    /**
     * Fungsi inti yang melakukan filter dan pencarian.
     * Logika ini tidak perlu diubah.
     */
    private fun applyFiltersAndSearch() {
        val original = _originalList.value ?: emptyList()
        val query = _searchQuery.value?.lowercase()?.trim() ?: ""
        val categories = _activeCategories.value ?: emptyList()

        val filteredList = original.filter { item ->
            // Kondisi 1: Pencarian berdasarkan teks
            val searchCondition = if (query.isEmpty()) {
                true
            } else {
                item.namaBarang?.lowercase()?.contains(query) == true ||
                        item.kategori?.lowercase()?.contains(query) == true
            }

            // Kondisi 2: Filter berdasarkan kategori
            val filterCondition = if (categories.isEmpty()) {
                true
            } else {
                categories.contains(item.kategori)
            }

            searchCondition && filterCondition
        }
        filteredPenemuanList.value = filteredList
    }


    // FUNGSI PUBLIK (TIDAK BERUBAH)
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setActiveCategories(categories: List<String>) {
        _activeCategories.value = categories
    }
}
