package com.example.binger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.binger.databinding.ActivityForgotpassBinding
import com.google.firebase.auth.FirebaseAuth

class forgotpass : AppCompatActivity() {

    private lateinit var binding: ActivityForgotpassBinding

    private lateinit var  firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotpassBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()

        binding.backLoginBtn.setOnClickListener {
            backToLogin()
        }

        binding.resetButton.setOnClickListener {
            recoverPass()
        }

        setupFieldValidations()

    }

    private fun backToLogin(){
        startActivity(Intent(this, Login::class.java))
    }

    private fun setupFieldValidations() {

        binding.resetUseremail.doOnTextChanged { _, _, _, _ ->
            validateEmailField()
        }
    }

    private fun validateEmailField() {
        val useremailText = binding.resetUseremail.text.toString().trim()

        if (useremailText.isBlank()) {
            binding.resetEmail.error = "This field is required"
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(useremailText).matches()){
            binding.resetEmail.error = "Invalid Email Pattern"
        }
        else {
            binding.resetEmail.error = null
        }
    }

    private fun recoverPass() {
        //show progress
        val useremailText = binding.resetUseremail.text.toString().trim()

        firebaseAuth.sendPasswordResetEmail(useremailText)
            .addOnSuccessListener {
                //sent

                Toast.makeText(this, "Instructions sent to \n $useremailText", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                //failed

                Toast.makeText(this, "Failed to send die to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}