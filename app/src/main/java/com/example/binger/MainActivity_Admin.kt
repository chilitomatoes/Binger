package com.example.binger

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class MainActivity_Admin : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        replaceFragment(RestaurantFragment(), title.toString())

        navView.setNavigationItemSelectedListener {

            it.isChecked = true

            when(it.itemId){
                R.id.nav_showRes -> replaceFragment(RestaurantFragment(), it.title.toString())
                R.id.nav_addRes -> replaceFragment(AddRestaurantFragment(), it.title.toString())
                R.id.nav_editRes -> replaceFragment(UpdateRestaurantFragment(), it.title.toString())
                R.id.nav_delRes -> replaceFragment(DeleteRestaurantFragment(), it.title.toString())
                R.id.nav_showFood -> replaceFragment(FoodFragment(), it.title.toString())
                R.id.nav_addFood -> replaceFragment(AddFoodFragment(), it.title.toString())
                R.id.nav_editFood -> replaceFragment(UpdateFoodFragment(), it.title.toString())
                R.id.nav_delFood -> replaceFragment(DeleteFoodFragment(), it.title.toString())
                R.id.nav_adminLogOut -> logout()
            }
            true
        }
    }

    private fun logout() {
        // Clear the login status
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        // Navigate back to the login screen
        startActivity(Intent(this, Login::class.java))
        finish()
    }

    private fun replaceFragment(fragment : Fragment, title : String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}