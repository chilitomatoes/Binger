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
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.binger.databinding.ActivityMainBinding
import com.example.binger.model.User
import com.example.binger.ui.menu.menuViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var  firebaseAuth: FirebaseAuth
    private lateinit var viewModel: menuViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
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

    public fun readUserData(): User {
        val json = sharedPreferences.getString("loginedUser", null)
        val gson = Gson()
        return gson.fromJson(json, User::class.java)
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
        if(item.itemId == R.id.action_settings){
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.nav_payment)
        }
        return super.onOptionsItemSelected(item)
    }
}