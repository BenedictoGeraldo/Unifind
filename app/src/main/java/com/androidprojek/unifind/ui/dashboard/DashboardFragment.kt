package com.androidprojek.unifind.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidprojek.unifind.R
import com.androidprojek.unifind.databinding.FragmentDashboardBinding
import com.androidprojek.unifind.model.Tracking
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private lateinit var trackingAdapter: TrackingAdapter
    private val trackingList = mutableListOf<Tracking>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        trackingAdapter = TrackingAdapter(trackingList)
        binding.rvTrackings.layoutManager = LinearLayoutManager(context)
        binding.rvTrackings.adapter = trackingAdapter

        binding.btnLacak.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_pelacakan_to_addTrackingFragment)
        }

        binding.fabTambah.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_pelacakan_to_addTrackingFragment)
        }

        // Lifecycle-aware listener using callbackFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                val snapshotFlow = callbackFlow {
                    val listenerRegistration = db.collection("trackings")
                        .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                close(e)
                                return@addSnapshotListener
                            }
                            trySend(snapshot).isSuccess
                        }

                    awaitClose { listenerRegistration.remove() }
                }

                snapshotFlow.collectLatest { snapshot ->
                    trackingList.clear()
                    for (document in snapshot?.documents ?: emptyList()) {
                        val tracking = document.toObject(Tracking::class.java)?.copy(id = document.id)
                        if (tracking != null) {
                            trackingList.add(tracking)
                        }
                    }

                    if (trackingList.isEmpty()) {
                        binding.empty.visibility = View.VISIBLE
                        binding.rvTrackings.visibility = View.GONE
                    } else {
                        binding.empty.visibility = View.GONE
                        binding.rvTrackings.visibility = View.VISIBLE
                        trackingAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
