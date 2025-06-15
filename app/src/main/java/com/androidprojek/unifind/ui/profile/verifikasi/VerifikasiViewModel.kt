package com.androidprojek.unifind.ui.profile.verifikasi

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VerifikasiViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // LiveData untuk memberi tahu Fragment jika proses selesai
    private val _prosesSelesai = MutableLiveData<Boolean>()
    val prosesSelesai: LiveData<Boolean> = _prosesSelesai

    // LiveData untuk menampilkan pesan error
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun terimaKlaim(postId: String, klaimDiterimaId: String) {
        viewModelScope.launch {
            try {
                val klaimRef = db.collection("form_penemuan").document(postId)
                    .collection("klaim_barang")

                // 1. Dapatkan semua klaim untuk postingan ini
                val semuaKlaimSnapshot = klaimRef.get().await()

                // Mulai operasi batch (semua berhasil atau semua gagal)
                val batch = db.batch()

                // 2. Loop melalui semua klaim
                for (dokumenKlaim in semuaKlaimSnapshot.documents) {
                    if (dokumenKlaim.id == klaimDiterimaId) {
                        // Jika ini adalah klaim yang diterima, update statusnya
                        batch.update(dokumenKlaim.reference, "statusKlaim", "Diterima")
                    } else {
                        // Jika bukan, tolak semua klaim lainnya
                        batch.update(dokumenKlaim.reference, "statusKlaim", "Ditolak")
                    }
                }

                // 3. Update status postingan utama menjadi "Selesai"
                val postinganUtamaRef = db.collection("form_penemuan").document(postId)
                batch.update(postinganUtamaRef, "status", "Selesai")

                // 4. Jalankan semua operasi sekaligus
                batch.commit().await()

                // Beri tahu Fragment bahwa proses sudah selesai
                _prosesSelesai.value = true

            } catch (e: Exception) {
                Log.e("VerifikasiViewModel", "Gagal menerima klaim", e)
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            }
        }
    }
}
