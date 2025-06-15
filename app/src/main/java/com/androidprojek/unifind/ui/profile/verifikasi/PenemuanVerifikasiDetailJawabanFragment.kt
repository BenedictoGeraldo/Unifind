package com.androidprojek.unifind.ui.profile.verifikasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.androidprojek.unifind.databinding.PenemuanVerifikasiDetailJawabanBinding
import com.androidprojek.unifind.model.PenemuanKlaimModel
import com.bumptech.glide.Glide

class PenemuanVerifikasiDetailJawabanFragment : Fragment() {

    private var _binding: PenemuanVerifikasiDetailJawabanBinding? = null
    private val binding get() = _binding!!

    // --- 1. INISIALISASI VIEWMODEL ---
    private val viewModel: VerifikasiViewModel by viewModels()

    private var dataKlaim: PenemuanKlaimModel? = null
    // Tambahkan properti untuk postId, kita akan butuh ini
    private var postId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ambil data yang dikirim dari halaman sebelumnya
        arguments?.let {
            postId = it.getString("postId")
            dataKlaim = it.getParcelable("dataKlaim")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PenemuanVerifikasiDetailJawabanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateView()
        setupClickListeners()
        observeViewModel() // Mulai mengamati perubahan dari ViewModel
    }

    // --- 2. FUNGSI BARU UNTUK MENGAMATI HASIL PROSES ---
    private fun observeViewModel() {
        viewModel.prosesSelesai.observe(viewLifecycleOwner) { selesai ->
            if (selesai) {
                Toast.makeText(context, "Verifikasi berhasil!", Toast.LENGTH_SHORT).show()
                // Kembali ke halaman daftar pengklaim
                findNavController().popBackStack()
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if(error.isNotEmpty()){
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun populateView() {
        dataKlaim?.let { klaim ->
            binding.toolbarDetailJawaban.title = "Jawaban dari ${klaim.namaPengklaim}"
            binding.tvDetailNamaPengklaim.text = klaim.namaPengklaim ?: "-"
            binding.tvDetailNimPengklaim.text = klaim.nimPengklaim ?: "-"
            binding.chipDetailStatusVerifikasi.text = klaim.statusKlaim ?: "Status Tidak Ada"

            binding.tvDetailNamaBarang.text = klaim.namaBarangKlaim ?: "-"
            binding.tvDetailKategori.text = klaim.kategoriKlaim ?: "-"
            binding.tvDetailDeskripsi.text = klaim.deskripsiKlaim ?: "-"
            binding.tvDetailTanggal.text = klaim.tanggalHilangKlaim ?: "-"
            binding.tvDetailWaktu.text = klaim.waktuHilangKlaim ?: "-"
            binding.tvDetailLokasi.text = klaim.lokasiHilangKlaim ?: "-"

            if (klaim.imageUrlKlaim.isNullOrEmpty()) {
                binding.ivDetailFoto.visibility = View.GONE
                binding.tvDetailFotoKosong.visibility = View.VISIBLE
            } else {
                binding.ivDetailFoto.visibility = View.VISIBLE
                binding.tvDetailFotoKosong.visibility = View.GONE
                Glide.with(this).load(klaim.imageUrlKlaim).into(binding.ivDetailFoto)
            }
        }
    }

    private fun setupClickListeners() {
        binding.toolbarDetailJawaban.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // --- 3. UBAH LOGIKA TOMBOL TERIMA ---
        binding.btnTerimaKlaim.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {
        val currentPostId = postId
        val currentKlaim = dataKlaim
        if (currentPostId != null && currentKlaim?.id != null) {
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Tindakan")
                .setMessage("Apakah Anda yakin ingin menerima klaim dari ${currentKlaim.namaPengklaim}? Tindakan ini akan menolak klaim lainnya dan tidak dapat diubah.")
                .setPositiveButton("Ya, Terima") { dialog, _ ->
                    // Panggil fungsi di ViewModel untuk memulai proses
                    viewModel.terimaKlaim(currentPostId, currentKlaim.id!!)
                    dialog.dismiss()
                }
                .setNegativeButton("Batal", null)
                .show()
        } else {
            Toast.makeText(context, "Error: Data tidak lengkap untuk verifikasi.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
