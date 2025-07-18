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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.androidprojek.unifind.ui.profile.KontakActivity
import com.androidprojek.unifind.LoginActivity
import com.androidprojek.unifind.R
import com.androidprojek.unifind.databinding.FragmentProfileBinding
import com.androidprojek.unifind.model.UserModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uploadProfileImage(it) }
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

        binding.btnKontak.setOnClickListener {
            startActivity(Intent(requireContext(), KontakActivity::class.java))
        }

        // --- PERUBAHAN UTAMA DI SINI ---
        binding.btnLacakFormulir.setOnClickListener {
            // Menggunakan NavController untuk berpindah ke halaman Lacak Formulir
            findNavController().navigate(R.id.action_profileFragment_to_profileLacakFormulirFragment)
        }
        // --- SELESAI PERUBAHAN ---

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    val document = db.collection("users").document(user.uid).get().await()

                    if (document.exists()) {
                        val userProfile = document.toObject(UserModel::class.java)
                        binding.tvNama.text = userProfile?.nama
                        binding.tvNim.text = userProfile?.nim

                        if (userProfile?.photoUrl?.isNotEmpty() == true) {
                            Glide.with(this@ProfileFragment).load(userProfile.photoUrl).into(binding.ivProfile)
                        } else {
                            Glide.with(this@ProfileFragment).load(R.drawable.baseline_person_outline_24).into(binding.ivProfile)
                        }
                    }
                } catch (e: CancellationException) {
                    // Ini normal terjadi saat pindah halaman, tidak perlu di-handle
                } catch (e: Exception) {
                    Toast.makeText(context, "Gagal memuat profil.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadProfileImage(imageUri: Uri) {
        setLoading(true)
        val user = auth.currentUser ?: return
        val storageRef = storage.reference.child("profile_pictures/${user.uid}")

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                storageRef.putFile(imageUri).await()
                val downloadUrl = storageRef.downloadUrl.await()
                saveImageUrlToFirestore(downloadUrl.toString())
            } catch (e: Exception) {
                setLoading(false)
                Toast.makeText(context, "Gagal mengunggah foto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        val user = auth.currentUser ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                db.collection("users").document(user.uid).update("photoUrl", imageUrl).await()
                Toast.makeText(context, "Foto profil berhasil diperbarui.", Toast.LENGTH_SHORT).show()
                Glide.with(this@ProfileFragment).load(imageUrl).into(binding.ivProfile)
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal menyimpan URL foto: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        // Pengecekan tambahan untuk mencegah crash
        _binding?.let {
            it.profileProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            it.ivEditProfile.isEnabled = !isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
