package com.androidprojek.unifind.ui.profile.verifikasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androidprojek.unifind.databinding.PenemuanVerifikasiDetailJawabanBinding // <-- NAMA BINDING DISESUAIKAN
import com.androidprojek.unifind.model.PenemuanKlaimModel
import com.bumptech.glide.Glide

// --- NAMA CLASS DISESUAIKAN ---
class PenemuanVerifikasiDetailJawabanFragment : Fragment() {

    // --- TIPE BINDING DISESUAIKAN ---
    private var _binding: PenemuanVerifikasiDetailJawabanBinding? = null
    private val binding get() = _binding!!

    private var dataKlaim: PenemuanKlaimModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dataKlaim = it.getParcelable("dataKlaim")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // --- INFLATE LAYOUT DISESUAIKAN ---
        _binding = PenemuanVerifikasiDetailJawabanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateView()
        setupClickListeners()
    }

    private fun populateView() {
        dataKlaim?.let { klaim ->
            binding.tvDetailNamaPengklaim.text = klaim.namaPengklaim ?: "-"
            binding.tvDetailNimPengklaim.text = klaim.nimPengklaim ?: "-"
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

        binding.btnTerimaKlaim.setOnClickListener {
            Toast.makeText(context, "Klaim Diterima", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
