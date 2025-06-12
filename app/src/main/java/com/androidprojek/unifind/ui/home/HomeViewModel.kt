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

    private fun listenToFirestoreChanges() {
        val currentUserUid = auth.currentUser?.uid

        if (currentUserUid == null) {
            _originalList.value = emptyList()
            return
        }

        db.collection("form_penemuan")
            .whereNotEqualTo("uid", currentUserUid)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("HomeViewModel", "Gagal mendengarkan data Firestore.", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val penemuanList = mutableListOf<PenemuanModel>()
                    // --- PERUBAHAN UTAMA DI SINI ---
                    for (document in snapshots.documents) {
                        // 1. Ubah dokumen menjadi objek PenemuanModel
                        val item = document.toObject(PenemuanModel::class.java)
                        if (item != null) {
                            // 2. Ambil ID dokumen dan masukkan ke dalam properti 'id'
                            item.id = document.id
                            penemuanList.add(item)
                        }
                    }
                    _originalList.value = penemuanList
                } else {
                    _originalList.value = emptyList()
                }
            }
    }

    private fun applyFiltersAndSearch() {
        val original = _originalList.value ?: emptyList()
        val query = _searchQuery.value?.lowercase()?.trim() ?: ""
        val categories = _activeCategories.value ?: emptyList()

        val filteredList = original.filter { item ->
            val searchCondition = if (query.isEmpty()) {
                true
            } else {
                item.namaBarang?.lowercase()?.contains(query) == true ||
                        item.kategori?.lowercase()?.contains(query) == true
            }

            val filterCondition = if (categories.isEmpty()) {
                true
            } else {
                categories.contains(item.kategori)
            }

            searchCondition && filterCondition
        }
        filteredPenemuanList.value = filteredList
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setActiveCategories(categories: List<String>) {
        _activeCategories.value = categories
    }
}
