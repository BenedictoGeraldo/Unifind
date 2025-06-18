package com.androidprojek.unifind.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.adapter.NotificationAdapter
import com.androidprojek.unifind.databinding.FragmentNotificationsBinding
import com.androidprojek.unifind.viewmodel.NotificationMainViewModel

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    // Dapatkan referensi ke ViewModel yang sama dengan fragment form
    private val sharedViewModel: NotificationMainViewModel by activityViewModels()

    // Deklarasikan adapter di level kelas agar bisa diakses di beberapa fungsi
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel() // Mulai 'mendengarkan' perubahan dari ViewModel
    }

    private fun setupRecyclerView() {
        // Inisialisasi adapter dengan daftar kosong pada awalnya.
        // Datanya akan diisi oleh observer.
        notificationAdapter = NotificationAdapter(emptyList())
        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notificationAdapter
        }
    }

    private fun observeViewModel() {
        sharedViewModel.notificationList.observe(viewLifecycleOwner) { notifications ->
            // Cek apakah daftar notifikasi yang diterima kosong atau tidak
            if (notifications.isEmpty()) {
                // Jika KOSONG:
                binding.rvNotifications.visibility = View.GONE // Sembunyikan RecyclerView
                binding.tvEmptyNotification.visibility = View.VISIBLE // Tampilkan teks "kosong"
            } else {
                // Jika ADA ISINYA:
                binding.rvNotifications.visibility = View.VISIBLE // Tampilkan RecyclerView
                binding.tvEmptyNotification.visibility = View.GONE // Sembunyikan teks "kosong"
                notificationAdapter.updateData(notifications) // Update data di adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}