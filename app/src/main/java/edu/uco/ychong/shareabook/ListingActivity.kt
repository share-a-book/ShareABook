package edu.uco.ychong.shareabook

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth

class ListingActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)


        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth?.currentUser

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.listing_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                true
            }
            R.id.menu_add_book -> {
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
