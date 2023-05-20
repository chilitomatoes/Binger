package com.example.binger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.binger.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class signup : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        firebaseAuth = FirebaseAuth.getInstance()

        binding.submitButton.setOnClickListener { createUserAccount() }

        binding.loginBtn.setOnClickListener {
            loginUser()
        }

        setupFieldValidations()
    }

    private fun setupFieldValidations() {
        binding.loginUsername.doOnTextChanged { _, _, _, _ ->
            validateUsernameField()
        }

        binding.contact.doOnTextChanged { _, _, _, _ ->
            validateContactField()
        }

        binding.password.doOnTextChanged { _, _, _, _ ->
            validatePasswordField()
        }

        binding.email.doOnTextChanged { _, _, _, _ ->
            validateEmailField()
        }
    }

    private fun validateUsernameField() {
        val usernameText = binding.loginUsername.text.toString().trim()

        if (usernameText.isBlank()) {
            binding.username.error = "This field is required"
        } else {
            binding.username.error = null
            if (usernameText.length > 20) {
                binding.username.error = "Exceeded maximum name length"
            }
        }
    }

    private fun validateContactField() {
        val usercontactText = binding.contact.text.toString().trim()

        if (usercontactText.isBlank()) {
            binding.usercontact.error = "This field is required"
        } else if (usercontactText.length > 11) {
            binding.usercontact.error = "Phone number should not exceed 11 digits"

        }else if (usercontactText.length < 10) {
            binding.usercontact.error = "Phone number should not less than 10 digits"
        } else {
            binding.usercontact.error = null
        }
    }

    private fun validatePasswordField() {
        val passwordText = binding.password.text.toString().trim()

        if (passwordText.isBlank()) {
            binding.userpassword.error = "This field is required"
        } else {
            binding.userpassword.error = null
        }
    }

    private fun validateEmailField() {
        val useremailText = binding.email.text.toString().trim()

        if (useremailText.isBlank()) {
            binding.userEmail.error = "This field is required"
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(useremailText).matches()){
            binding.userEmail.error = "Invalid Email Pattern"
        }
        else {
            binding.userEmail.error = null
        }
    }

    private fun createUserAccount() {
        val useremailText = binding.email.text.toString().trim()
        val passwordText = binding.password.text.toString().trim()

        if (!validateFields()) {

            return
        }

        // Create user in Firebase Auth
        firebaseAuth.createUserWithEmailAndPassword(useremailText, passwordText)
            .addOnSuccessListener {
                // Account created, now add user info in the database
                updateUserInfo()
            }
            .addOnFailureListener { e ->
                // Failed creating account
                Toast.makeText(this, "Failed creating account due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateFields(): Boolean {

        validateUsernameField()
        validateContactField()
        validatePasswordField()
        validateEmailField()

        return binding.username.error == null &&
                binding.usercontact.error == null &&
                binding.userpassword.error == null &&
                binding.userEmail.error == null
    }

    private fun updateUserInfo() {
        val usernameText = binding.loginUsername.text.toString().trim()
        val usercontactText = binding.contact.text.toString().trim()
        val useremailText = binding.email.text.toString().trim()


        // Get current user UID, since the user is registered, we can get it now
        val uid = firebaseAuth.uid

        // Setup data to add in the database
        val accInfo: HashMap<String, Any?> = HashMap()
        accInfo["uid"] = uid
        accInfo["email"] = useremailText
        accInfo["username"] = usernameText
        accInfo["userContact"] = usercontactText
        accInfo["cards"] = ""
        accInfo["addresses"] = ""
        accInfo["orders"] = ""


        // Set data to the database
        val ref = FirebaseDatabase.getInstance().getReference("User")
        ref.child(uid!!).setValue(accInfo).addOnSuccessListener {
            Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Login::class.java))
            finish()
        }

            .addOnFailureListener { e ->
                // Failed adding data to the database
                Toast.makeText(this, "Failed saving user info due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loginUser() {
        val login = Intent(this, Login::class.java)
        startActivity(login)
    }
}