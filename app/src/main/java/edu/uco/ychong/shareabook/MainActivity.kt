package edu.uco.ychong.shareabook

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
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

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun setLoginLogoutStats(currentUser: FirebaseUser?) {
        val nav_view = findViewById<NavigationView>(R.id.nav_view)
        val nav_menu = nav_view.menu

        if(currentUser == null) {
            nav_menu.findItem(R.id.nav_login).setVisible(true)
            nav_menu.findItem(R.id.nav_accountInfo).setVisible(false)
            nav_menu.findItem(R.id.nav_logout).setVisible(false)
        } else {
            nav_menu.findItem(R.id.nav_login).setVisible(false)
            nav_menu.findItem(R.id.nav_accountInfo).setVisible(true)
            nav_menu.findItem(R.id.nav_logout).setVisible(true)
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

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
