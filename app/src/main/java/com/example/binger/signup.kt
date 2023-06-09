package com.example.binger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
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

        val loginText = "Already Registered , Sign In !"
        val spannableStringSign = SpannableString(loginText)
        val clickableSpanSign = object : ClickableSpan() {
            override fun onClick(view: View) {
                val intent = Intent(this@signup, Login::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(this@signup, R.color.red) // Set custom color
            }
        }

        spannableStringSign.setSpan(clickableSpanSign, 20, loginText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.loginBtn.text =  spannableStringSign
        binding.loginBtn.movementMethod = LinkMovementMethod.getInstance()

        binding.submitButton.setOnClickListener { createUserAccount() }



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

        val uid = firebaseAuth.uid

        val accInfo: HashMap<String, Any?> = HashMap()
        accInfo["uid"] = uid
        accInfo["email"] = useremailText
        accInfo["username"] = usernameText
        accInfo["userContact"] = usercontactText
        accInfo["cards"] = ""
        accInfo["addresses"] = ""
        accInfo["orders"] = ""


        val ref = FirebaseDatabase.getInstance().getReference("User")
        ref.child(uid!!).setValue(accInfo).addOnSuccessListener {
            Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
            firebaseAuth.signOut()
            startActivity(Intent(this, Login::class.java))
            finish()
        }

            .addOnFailureListener { e ->
                // Failed adding data to the database
                Toast.makeText(this, "Failed saving user info due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


}