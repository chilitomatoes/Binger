package com.example.binger

import android.content.ContentValues.TAG
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
import android.util.Log
import com.example.binger.databinding.ActivityLoginBinding
import com.example.binger.model.Address
import com.example.binger.model.Order
import com.example.binger.model.PaymentMethod
import com.example.binger.model.User
import com.google.gson.Gson


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var  firebaseAuth: FirebaseAuth

    private lateinit var sharedPreferences: SharedPreferences

    private val adminRef = FirebaseDatabase.getInstance().getReference("Admin")

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

        adminRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var isAdmin = false
                if (dataSnapshot.exists()) {
                    val adminEmail = dataSnapshot.child("email").getValue().toString()
                    Log.v("@", adminEmail)
                    val adminPassword = dataSnapshot.child("password").getValue().toString()
                    Log.v("@", adminPassword)

                    if (adminEmail == useremailText && adminPassword == passwordText) {
                        isAdmin = true
                    }
                }

                if (isAdmin) {
                    startActivity(Intent(this@Login, MainActivity_Admin::class.java))
                    finish()
                } else {
                    // Admin credentials not found or didn't match
                    // Continue with regular user login process
                    loginUserWithFirebaseAuth(useremailText, passwordText)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error here if needed
            }
        })


    }

    private fun loginUserWithFirebaseAuth(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // Login success
                checkUser()
            }
            .addOnFailureListener { e ->
                // Failed login
                Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
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


                var cardList: ArrayList<PaymentMethod> = ArrayList()
                for(cardSnapShot in snapshot.child("cards").children){
                    val card = cardSnapShot.getValue(PaymentMethod::class.java)
                    cardList.add(card!!)
                }

                var addressList: ArrayList<Address> = ArrayList()
                for(addressSnapShot in snapshot.child("addresses").children){
                    val address = addressSnapShot.getValue(Address::class.java)
                    addressList.add(address!!)
                }

                var orderList: ArrayList<Order> = ArrayList()
                for(orderSnapShot in snapshot.child("cards").children){
                    val order = orderSnapShot.getValue(Order::class.java)
                    orderList.add(order!!)
                }
                val username = snapshot.child("username").value.toString()
                val email = snapshot.child("email").value.toString()
                val contact = snapshot.child("userContact").value.toString()


                val loginedUser: User = User(firebaseUser.uid,email,username,contact,cardList,addressList,orderList)

                // Save user data to SharedPreferences
                if (loginedUser != null) {
                    saveUserData(loginedUser)
                }


                // Navigate to the profile screen
                startActivity(Intent(this@Login,MainActivity::class.java))
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun saveUserData(loginedUser: User) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(loginedUser)
        editor.putString("loginedUser", json)
        editor.apply()
    }

    private fun signUser(){
        startActivity(Intent(this, signup::class.java))
    }

    private fun forgotpass(){
        startActivity(Intent(this, forgotpass::class.java))
    }

}