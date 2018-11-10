package edu.uco.ychong.shareabook.user

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import edu.uco.ychong.shareabook.MainActivity
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.fragments.BookAddFragment
import edu.uco.ychong.shareabook.book.fragments.BookListingFragment
import edu.uco.ychong.shareabook.helper.ToastMe


const val BOOK_ADD_FRAGMENT = "bookAddFragment"
const val BOOK_LISTING_FRAGMENT = "bookListingFragment"

class ListingActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth?= null
    private val fragmentManager = supportFragmentManager
    private var addBookSuccess : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)
        mAuth = FirebaseAuth.getInstance()

        displayBookListingFragment()
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
        displayBookListingFragment()
        ToastMe.message(this, "Added successfully")
    }

    fun addBookFail() {
        ToastMe.message(this, "Added fail")
    }

    override fun onResume() {
        if (addBookSuccess)
            ToastMe.message(this, "Added Successfully")
        super.onResume()
    }

    private fun displayBookAddFragment() {
        if (isDestroyed) return
        val fragment = BookAddFragment()
        val bookAddFragment = fragmentManager.findFragmentByTag(BOOK_ADD_FRAGMENT)
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out
        )
        if (bookAddFragment == null) {
            fragmentTransaction.add(R.id.book_container, fragment, BOOK_ADD_FRAGMENT)
            fragmentTransaction.commit()
        }
        removeBookListingFragment()
    }

    private fun removeBookAddFragment() {
        val bookAddFragment = fragmentManager.findFragmentByTag(BOOK_ADD_FRAGMENT)
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (bookAddFragment != null) {
            fragmentTransaction.remove(bookAddFragment)
            fragmentTransaction.commit()
        }
    }

    private fun displayBookListingFragment() {
        if (isDestroyed) return
        val fragment = BookListingFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        val bookListingFragment = fragmentManager.findFragmentByTag(BOOK_LISTING_FRAGMENT)
        fragmentTransaction.setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out
        )
        if (bookListingFragment == null) {
            fragmentTransaction.add(R.id.book_container, fragment, BOOK_LISTING_FRAGMENT)
            fragmentTransaction.commit()
        }
        removeBookAddFragment()
    }

    private fun removeBookListingFragment() {
        val bookListingFragment = fragmentManager.findFragmentByTag(BOOK_LISTING_FRAGMENT)
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (bookListingFragment != null) {
            fragmentTransaction.remove(bookListingFragment)
            fragmentTransaction.commit()
        }
    }
}
