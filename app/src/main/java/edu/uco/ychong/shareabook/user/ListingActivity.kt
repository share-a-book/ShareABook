package edu.uco.ychong.shareabook.user

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import edu.uco.ychong.shareabook.MainActivity
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.BookAddFragment
import edu.uco.ychong.shareabook.book.BookListingFragment
import edu.uco.ychong.shareabook.helper.ToastMe

class ListingActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth?= null
    private val fragmentManager = supportFragmentManager
    private var addBookSuccess : Boolean = false

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
            R.id.menu_listing -> {
                displayBookListingFragment()
                true
            }
            R.id.menu_add_book -> {
                displayBookAddFragment()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun addBookSuccess() {
        ToastMe.message(this, "Added successfully")
    }

    fun addBookFail() {
        ToastMe.message(this, "Added fail")
    }

    override fun onResume() {
        Log.d(TAG, "onResume()")

        if (addBookSuccess) {
            Log.d(TAG, addBookSuccess.toString())
            ToastMe.message(this, "Added Successfully")
        }
        super.onResume()
    }

    private fun displayBookListingFragment() {
        if (isDestroyed) return
        val fragment = BookListingFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.book_container, fragment, "bookListingFragment")
        fragmentTransaction.commit()
    }

    private fun displayBookAddFragment() {
        if (isDestroyed) return
        val fragment = BookAddFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.book_container, fragment, "bookAddFragment")
        fragmentTransaction.commit()
    }

    private fun removeAddBookFragment() {

    }


}
