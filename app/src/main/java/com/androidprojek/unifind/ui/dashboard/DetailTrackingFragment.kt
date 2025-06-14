package com.androidprojek.unifind.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.androidprojek.unifind.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*

class DetailTrackingFragment : Fragment(), OnMapReadyCallback {

    private var idPerangkat: String? = null
    private lateinit var googleMap: GoogleMap
    private var currentMarker: Marker? = null
    private lateinit var database: DatabaseReference

    // Simulasi koordinat
//    private val latitude = -6.200000
//    private val longitude = 106.816666

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idPerangkat = it.getString("idPerangkat")
        }
        database = FirebaseDatabase.getInstance().getReference("tracking/$idPerangkat")
        Log.d("DEBUG_TRACKING", "Reference to database initialized")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_tracking, container, false)

        // Tampilkan ID
        val tvIdPerangkat = view.findViewById<TextView>(R.id.tv_id_perangkat_detail)
        tvIdPerangkat.text = idPerangkat ?: "ID tidak ditemukan"

        // Inisialisasi map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Coba tambahkan marker dummy dulu
//        val dummyLocation = LatLng(-6.2, 106.816666)
//        googleMap.addMarker(MarkerOptions().position(dummyLocation).title("Dummy Marker"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dummyLocation, 15f))

        // Pantau perubahan data realtime
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.d("DEBUG_TRACKING", "Snapshot exists: ${snapshot.exists()}")
//                Log.d("DEBUG_TRACKING", "Snapshot children count: ${snapshot.childrenCount}")
//                Log.d("DEBUG_TRACKING", "Snapshot value: ${snapshot.value}")

                val lat = snapshot.child("latitude").getValue(Double::class.java)
                val lng = snapshot.child("longitude").getValue(Double::class.java)
//                Log.d("DEBUG_TRACKING", "Lat: $lat, Lng: $lng")

                if (lat != null && lng != null) {
                    val newLocation = LatLng(lat, lng)

                    if (currentMarker == null) {
                        currentMarker = googleMap.addMarker(
                            MarkerOptions().position(newLocation).title("Barang")
                        )
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15f))
                    } else {
                        currentMarker!!.position = newLocation
                        // Optional: animate camera
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(newLocation))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("DEBUG_TRACKING", "Database read cancelled: ${error.message}")
            }
        })
    }
}