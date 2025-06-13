package com.androidprojek.unifind.ui.profile.lacakformulir

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androidprojek.unifind.databinding.FragmentLacakDetailJawabanBinding
import com.androidprojek.unifind.model.PenemuanKlaimModel
import com.bumptech.glide.Glide

class LacakDetailJawabanFragment : Fragment() {

    private var _binding: FragmentLacakDetailJawabanBinding? = null
    private val binding get() = _binding!!

    // Properti untuk menampung data klaim yang diterima
    private var dataKlaim: PenemuanKlaimModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ambil data Parcelable dari argumen
        arguments?.let {
            dataKlaim = it.getParcelable("dataKlaim")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLacakDetailJawabanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Panggil fungsi untuk mengisi data ke tampilan
        populateView()

        // Atur listener untuk tombol kembali
        binding.toolbarDetailJawaban.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun populateView() {
        dataKlaim?.let { klaim ->
            binding.tvLacakDetailNama.text = klaim.namaPengklaim ?: "-"
            binding.tvLacakDetailNim.text = klaim.nimPengklaim ?: "-"
            binding.tvLacakDetailNamaBarang.text = klaim.namaBarangKlaim ?: "-"
            binding.tvLacakDetailKategori.text = klaim.kategoriKlaim ?: "-"
            binding.tvLacakDetailDeskripsi.text = klaim.deskripsiKlaim ?: "-"
            binding.tvLacakDetailTanggal.text = klaim.tanggalHilangKlaim ?: "-"
            binding.tvLacakDetailWaktu.text = klaim.waktuHilangKlaim ?: "-"
            binding.tvLacakDetailLokasi.text = klaim.lokasiHilangKlaim ?: "-"

            // Cek apakah ada URL foto bukti
            if (klaim.imageUrlKlaim.isNullOrEmpty()) {
                binding.ivLacakDetailFoto.visibility = View.GONE
                binding.tvLacakDetailFotoKosong.visibility = View.VISIBLE
            } else {
                binding.ivLacakDetailFoto.visibility = View.VISIBLE
                binding.tvLacakDetailFotoKosong.visibility = View.GONE
                Glide.with(this).load(klaim.imageUrlKlaim).into(binding.ivLacakDetailFoto)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
