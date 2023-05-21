package com.example.binger

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.binger.databinding.FragmentModifyPassBinding
import com.example.binger.databinding.FragmentProfileBinding
import com.google.firebase.auth.EmailAuthProvider

import com.google.firebase.auth.FirebaseAuth



class modifyPassFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private var _binding: FragmentModifyPassBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModifyPassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        binding.changePassButton.setOnClickListener { changePassword() }

    }


    private fun changePassword() {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email

        val oldPassword = binding.usernewpassword.text.toString().trim()
        val newPassword = binding.useroldpassword.text.toString().trim()

        if (user != null && email != null) {
            val credential = EmailAuthProvider.getCredential(email, oldPassword)

            // Reauthenticate the user to verify the old password
            user.reauthenticate(credential)
                .addOnSuccessListener {
                    // Reauthentication successful, proceed with password update
                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                            navigateToLogin()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Password update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Reauthentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }


}