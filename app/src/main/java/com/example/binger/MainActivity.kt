package com.example.binger

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.binger.databinding.ActivityMainBinding
import com.example.binger.databinding.NavHeaderMainBinding
import com.example.binger.model.GeocoderData
import com.example.binger.model.User
import com.example.binger.ui.address.AddressFragment
import com.example.binger.ui.menu.menuViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bindingNAVHEAD: NavHeaderMainBinding
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var viewModel: menuViewModel
    private lateinit var loginedUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("User")
        firebaseAuth = FirebaseAuth.getInstance()


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)


        binding = ActivityMainBinding.inflate(layoutInflater)

        bindingNAVHEAD= NavHeaderMainBinding.inflate(layoutInflater)

        setContentView(binding.root)




        setSupportActionBar(binding.appBarMain.toolbar)



        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_profile, R.id.nav_address, R.id.nav_payment, R.id.nav_logout, R.id.nav_profile
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val logoutButton: MenuItem = navView.menu.findItem(R.id.nav_logout)
        logoutButton.setOnMenuItemClickListener {
            logout()
            true
        }
        val viewHeader = binding.navView.getHeaderView(0)
        val navViewHeaderBinding : NavHeaderMainBinding = NavHeaderMainBinding.bind(viewHeader)


        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                loginedUser=readUserData()
                Log.v(ContentValues.TAG,"--------------------------------"+ loginedUser.username.toString())
                navViewHeaderBinding.HEaderEmail.text=loginedUser.email
                navViewHeaderBinding.HEaderName.text=loginedUser.username
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



    }

    private fun logout() {
        // Clear shared preferences data
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Sign out the user from Firebase Auth
        firebaseAuth.signOut()

        // Navigate back to the login screen
        startActivity(Intent(this, Login::class.java))
        finish()
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //if(item.itemId == R.id.action_settings){
        //    findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.nav_payment)
        //}
        return super.onOptionsItemSelected(item)
    }

    private fun saveUserData(loginedUser: User) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(loginedUser)
        editor.putString("loginedUser", json)
        editor.apply()
    }

    private fun readUserData(): User {
        val json = sharedPreferences.getString("loginedUser", null)
        val gson = Gson()
        return gson.fromJson(json, User::class.java)
    }

}