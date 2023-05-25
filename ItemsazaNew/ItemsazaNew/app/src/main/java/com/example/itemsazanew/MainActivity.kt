package com.example.itemsazanew

import android.content.Intent
import android.icu.text.CaseMap.Title
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout  = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

//        set Title
        setTitle("Organic Farm")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            it.isChecked = true

            when(it.itemId){

                R.id.nav_home -> replaceFragment(home(),it.title.toString())
                R.id.profile -> replaceFragment(profile(),it.title.toString())
                R.id.offers -> replaceFragment(offers(),it.title.toString())
                R.id.orders -> replaceFragment(orders(),it.title.toString())
                //navigate to the save card activity//
                R.id.card_details -> replaceFragment(showCards(),it.title.toString())
                R.id.nav_help -> Toast.makeText(applicationContext, "Clicked Help", Toast.LENGTH_SHORT ).show()
                R.id.nav_about -> Toast.makeText(applicationContext, "Clicked AboutUs", Toast.LENGTH_SHORT ).show()
                R.id.nav_logout -> signOutUser()

            }

            true
        }

    }

    //trans fragment according to user clicks
    private fun replaceFragment(fragment: Fragment,title: String){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()//close the drawer when moving to another fragment
        setTitle(title)//set the title

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){

            return true
        }


        return super.onOptionsItemSelected(item)
    }

    private fun signOutUser(){
        FirebaseAuth.getInstance().signOut()
        var intent = Intent(this,login::class.java)
        startActivity(intent)
        finish()
    }

}