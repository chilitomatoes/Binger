package com.example.binger

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.binger.databinding.FragmentProfileBinding
import com.example.binger.model.User

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson

class ProfileFragment : Fragment() {
    private lateinit var loginedUser: User
    private lateinit var sharedPreferences: SharedPreferences
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        loginedUser= readUserData()

        binding.setUserpassword.setText("1234567890")
        binding.settUsername.setText(loginedUser.username)
        binding.setUsercontact.setText((loginedUser.userContact))
        binding.settUseremail.setText(loginedUser.email)

        binding.updateButton.setOnClickListener { updateInfo() }
        binding.setUserpassword.setOnClickListener{
            findNavController().navigate(R.id.modifyPassFragment)
        }

        setupFieldValidations()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupFieldValidations() {
        binding.settUsername.doOnTextChanged { _, _, _, _ ->
            validateUsernameField()
        }

        binding.setUsercontact.doOnTextChanged { _, _, _, _ ->
            validateContactField()
        }

        binding.settUseremail.doOnTextChanged { _, _, _, _ ->
            validateEmailField()
        }
    }

    private fun validateUsernameField() {
        val usernameText = binding.settUsername.text.toString().trim()

        if (usernameText.isBlank()) {
            binding.textInputLayout.error = "This field is required"
        } else {
            binding.textInputLayout.error = null
            if (usernameText.length > 20) {
                binding.textInputLayout.error = "Exceeded maximum name length"
            }
        }
    }

    private fun validateContactField() {
        val usercontactText = binding.setUsercontact.text.toString().trim()

        if (usercontactText.isBlank()) {
            binding.contactInputLayout.error = "This field is required"

        } else if (usercontactText.length > 11) {
            binding.contactInputLayout.error = "Phone number should not exceed 11 digits"

        } else if (usercontactText.length < 10) {
            binding.contactInputLayout.error = "Phone number should not be less than 10 digits"
        } else {
            binding.contactInputLayout.error = null
        }
    }

    private fun validateEmailField() {
        val useremailText = binding.settUseremail.text.toString().trim()

        if (useremailText.isBlank()) {
            binding.textInputLayout3.error = "This field is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(useremailText).matches()) {
            binding.textInputLayout3.error = "Invalid Email Pattern"
        } else {
            binding.textInputLayout3.error = null
        }
    }

    private fun validateFields(): Boolean {
        validateUsernameField()
        validateContactField()
        validateEmailField()

        return binding.textInputLayout.error == null &&
                binding.contactInputLayout.error == null &&
                binding.textInputLayout3.error == null
    }

    private fun updateInfo() {
        val newUsername = binding.settUsername.text.toString()
        val newContact = binding.setUsercontact.text.toString()
        val newEmail = binding.settUseremail.text.toString()

        if (!validateFields()) {
            return
        }

        // Update Firebase database and authentication
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            // Update email in Firebase Authentication
            firebaseUser.updateEmail(newEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Email updated successfully
                        // Update email in Firebase Realtime Database
                        val ref = FirebaseDatabase.getInstance().getReference("User")
                        val userRef = ref.child(firebaseUser.uid)
                        userRef.child("email").setValue(newEmail)
                        // Update other fields in Firebase Realtime Database
                        userRef.child("username").setValue(newUsername)
                        userRef.child("userContact").setValue(newContact)
                        loginedUser.username = newUsername
                        loginedUser.email = newEmail
                        loginedUser.userContact = newContact
                        saveUserData(loginedUser)
                        Toast.makeText(requireContext(), "Changes saved", Toast.LENGTH_SHORT).show()


                    } else {

                        Toast.makeText(requireContext(), "Failed to update email", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {

            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }


    public fun readUserData(): User {
        val gson = Gson()
        val json = sharedPreferences.getString("loginedUser", null)
        return gson.fromJson(json, User::class.java)
    }

    private fun saveUserData(loginedUser: User) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(loginedUser)
        editor.putString("loginedUser", json)
        editor.apply()
    }
}
