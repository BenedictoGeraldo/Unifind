package com.androidprojek.unifind.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androidprojek.unifind.ui.profile.KontakActivity // <-- Pastikan import ini ada
import com.androidprojek.unifind.LoginActivity
import com.androidprojek.unifind.R
import com.androidprojek.unifind.databinding.FragmentProfileBinding
import com.androidprojek.unifind.model.UserModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            if (imageUri != null) {
                uploadProfileImage(imageUri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        loadUserProfile()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.ivEditProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            imagePickerLauncher.launch(intent)
        }

        binding.btnPostinganSaya.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileMyPostsFragment)
        }

        // --- PERUBAHAN UTAMA DI SINI ---
        binding.btnKontak.setOnClickListener {
            // Hapus Toast dan aktifkan Intent untuk memulai KontakActivity
            val intent = Intent(requireContext(), KontakActivity::class.java)
            startActivity(intent)
        }
        // --- SELESAI PERUBAHAN ---

        binding.btnLacakFormulir.setOnClickListener {
            Toast.makeText(requireContext(), "Fitur ini dalam pengembangan", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar dari akun ini?")
            .setPositiveButton("Ya, Keluar") { dialog, _ ->
                auth.signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun loadUserProfile() {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userProfile = document.toObject(UserModel::class.java)
                    binding.tvNama.text = userProfile?.nama
                    binding.tvNim.text = userProfile?.nim

                    if (userProfile?.photoUrl?.isNotEmpty() == true) {
                        Glide.with(this).load(userProfile.photoUrl).into(binding.ivProfile)
                    } else {
                        Glide.with(this).load(R.drawable.baseline_person_outline_24).into(binding.ivProfile)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal memuat profil.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadProfileImage(imageUri: Uri) {
        setLoading(true)
        val user = auth.currentUser ?: return
        val storageRef = storage.reference.child("profile_pictures/${user.uid}")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    saveImageUrlToFirestore(downloadUrl.toString())
                }
            }
            .addOnFailureListener { setLoading(false); Toast.makeText(requireContext(), "Gagal mengunggah foto.", Toast.LENGTH_SHORT).show() }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid)
            .update("photoUrl", imageUrl)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(requireContext(), "Foto profil berhasil diperbarui.", Toast.LENGTH_SHORT).show()
                Glide.with(this).load(imageUrl).into(binding.ivProfile)
            }
            .addOnFailureListener { setLoading(false); Toast.makeText(requireContext(), "Gagal menyimpan URL foto.", Toast.LENGTH_SHORT).show() }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.profileProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.ivEditProfile.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
