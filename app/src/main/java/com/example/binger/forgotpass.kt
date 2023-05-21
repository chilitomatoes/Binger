package com.example.binger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
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

        binding.resetButton.setOnClickListener {
            recoverPass()
        }

        val backloginText = "Back to Login"
        val spannableString = SpannableString(backloginText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                // Start the desired activity here
                val intent = Intent(this@forgotpass, Login::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false // Remove the underline
                ds.color = ContextCompat.getColor(this@forgotpass, R.color.red) // Set custom color
            }
        }

        spannableString.setSpan(clickableSpan, 0, backloginText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.backLoginBtn.text =  spannableString
        binding.backLoginBtn.movementMethod = LinkMovementMethod.getInstance()

        setupFieldValidations()

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

                Toast.makeText(this, "Failed to send due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}