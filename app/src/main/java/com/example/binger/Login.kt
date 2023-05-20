package com.example.binger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.binger.databinding.ActivityLoginBinding


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var  firebaseAuth: FirebaseAuth

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        binding.loginButton2.setOnClickListener {loginUser()}

        binding.signupBtn.setOnClickListener {
            signUser()
        }

        binding.forgotBtn.setOnClickListener() {
            forgotpass()
        }

        setupFieldValidations()
    }



    private fun setupFieldValidations() {

        binding.loginPassword.doOnTextChanged { _, _, _, _ ->
            validatePasswordField()
        }

        binding.loginUseremail.doOnTextChanged { _, _, _, _ ->
            validateEmailField()
        }
    }

    private fun validatePasswordField() {
        val passwordText = binding.loginPassword.text.toString().trim()

        if (passwordText.isBlank()) {
            binding.password.error = "This field is required"
        } else {
            binding.password.error = null
        }
    }

    private fun validateEmailField() {
        val useremailText = binding.loginUseremail.text.toString().trim()

        if (useremailText.isBlank()) {
            binding.loginEmail.error = "This field is required"
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(useremailText).matches()){
            binding.loginEmail.error = "Invalid Email Pattern"
        }
        else {
            binding.loginEmail.error = null
        }
    }


    private fun loginUser() {

        val useremailText = binding.loginUseremail.text.toString().trim()
        val passwordText = binding.loginPassword.text.toString().trim()

        if (!validateFields()) {
            return
        }
        //3 Login Firebase Auth
        //show progress

        firebaseAuth.signInWithEmailAndPassword(useremailText, passwordText)
            .addOnSuccessListener {
                //login success
                checkUser()
            }
            .addOnFailureListener{ e->
                //failed login
                Toast.makeText(this,"Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateFields(): Boolean {

        validatePasswordField()
        validateEmailField()

        return  binding.loginUseremail.error == null &&
                binding.password.error == null
    }


    /*private fun checkUser() {

        val firebaseUser = firebaseAuth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("User")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot){
                    startActivity(Intent(this@Login, profile::class.java))
                    finish()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }*/

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("User")
        ref.child(firebaseUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("username").value.toString()
                val email = snapshot.child("email").value.toString()
                val contact = snapshot.child("userContact").value.toString()

                // Save user data to SharedPreferences
                saveUserData(username, email, contact,)

                // Navigate to the profile screen
                startActivity(Intent(this@Login,MainActivity::class.java))
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun saveUserData(username: String, email: String, contact: String,) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("email", email)
        editor.putString("contact", contact)
        editor.apply()
    }

    private fun signUser(){
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun forgotpass(){
        startActivity(Intent(this, forgotpass::class.java))
    }

}