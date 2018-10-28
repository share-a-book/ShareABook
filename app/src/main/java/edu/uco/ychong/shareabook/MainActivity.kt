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
import edu.uco.ychong.shareabook.helper.UserAccess
import edu.uco.ychong.shareabook.user.AccountInfoActivity
import edu.uco.ychong.shareabook.user.ListingActivity
import edu.uco.ychong.shareabook.user.LoginActivity
import edu.uco.ychong.shareabook.user.SignUpActivity
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

        if (UserAccess.isLoggedIn(currentUser)) {
            val headerView = nav_view.getHeaderView(0)
            val emailView = headerView.findViewById<TextView>(R.id.id_nav_email)
            emailView.text = currentUser?.email
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

        // *** !!! Uncomment after complete testing !!! ***//

//        if(UserAccess.isLoggedIn(currentUser)) {
//            navMenu.findItem(R.id.nav_listing).isVisible = true
//            navMenu.findItem(R.id.nav_pending).isVisible = true
//            navMenu.findItem(R.id.nav_history).isVisible = true
//            navMenu.findItem(R.id.nav_logout).isVisible = true
//            navMenu.findItem(R.id.nav_accountInfo).isVisible = true
//
//            navMenu.findItem(R.id.nav_login).isVisible = false
//            navMenu.findItem(R.id.nav_sign_up).isVisible = false
//        } else {
//            navMenu.findItem(R.id.nav_listing).isVisible = false
//            navMenu.findItem(R.id.nav_pending).isVisible = false
//            navMenu.findItem(R.id.nav_history).isVisible = false
//            navMenu.findItem(R.id.nav_logout).isVisible = false
//            navMenu.findItem(R.id.nav_accountInfo).isVisible = false
//
//            navMenu.findItem(R.id.nav_login).isVisible = true
//            navMenu.findItem(R.id.nav_sign_up).isVisible = true
//        }
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
        val currentUser = mAuth?.currentUser
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_books -> {

            }
            R.id.nav_listing -> {
                startActivity(Intent(this, ListingActivity::class.java))
            }
            R.id.nav_pending-> {

            }
            R.id.nav_history -> {

            }
            R.id.nav_sign_up -> {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
            R.id.nav_login -> {
                if(!UserAccess.isLoggedIn(currentUser)) {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            R.id.nav_accountInfo -> {
                if(UserAccess.isLoggedIn(currentUser)) {
                    startActivity(Intent(this, AccountInfoActivity::class.java))
                }
            }
            R.id.nav_logout -> {
                if(UserAccess.isLoggedIn(currentUser)) {
                    mAuth?.signOut()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
