package edu.uco.ychong.shareabook

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mAuth: FirebaseAuth ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth?.currentUser

        if (currentUser != null) {
            val headerView = nav_view.getHeaderView(0)
            val emailView = headerView.findViewById<TextView>(R.id.id_nav_email)
            emailView.text = currentUser.email
        }
        setLoginLogoutStats(currentUser)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun setLoginLogoutStats(currentUser: FirebaseUser?) {
        val navMenu = findViewById<NavigationView>(R.id.nav_view).menu
        if(UserAccess.isLoggedIn(currentUser)) {
            navMenu.findItem(R.id.nav_login).isVisible = false
            navMenu.findItem(R.id.nav_accountInfo).isVisible = true
            navMenu.findItem(R.id.nav_logout).isVisible = true
        } else {
            navMenu.findItem(R.id.nav_login).isVisible = true
            navMenu.findItem(R.id.nav_accountInfo).isVisible = false
            navMenu.findItem(R.id.nav_logout).isVisible = false
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            //mAuth?.signOut()
            //Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_books -> {

            }
            R.id.nav_listing -> {

            }
            R.id.nav_pending-> {

            }
            R.id.nav_history -> {

            }
            R.id.nav_login -> {
                val currentUser = mAuth?.currentUser

                if(currentUser == null) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.nav_accountInfo -> {
                val currentUser = mAuth?.currentUser
                if(currentUser != null) {
                    val intent = Intent(this, AccountInfoActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.nav_logout -> {
                val currentUser = mAuth?.currentUser
                if(currentUser != null) {
                    mAuth?.signOut()
                    finish()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
