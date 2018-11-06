package edu.uco.ychong.shareabook

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.book.*
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.helper.UserAccess
import edu.uco.ychong.shareabook.model.Book
import edu.uco.ychong.shareabook.model.User
import edu.uco.ychong.shareabook.user.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

const val USER_INFO = "user_info"
const val UPDATED_USER_INFO = "updated_user_info"
const val REQ_CODE_EDIT_ACCOUNT_INFO = 1
const val EXTRA_SELECTED_BOOK = "extra_selected_book"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mAuth: FirebaseAuth? = null
    private var mFireStore: FirebaseFirestore? = null
    var availableBooks = ArrayList<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        val currentUser = mAuth?.currentUser

        if (UserAccess.isLoggedIn(currentUser)) {
            val email = currentUser?.email
            if (email == null) return
            setAccountHeaderInfo(email)
        }

        setNavMenuLoginLogoutVisibility(currentUser)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        var viewManager = LinearLayoutManager(this)

        val bookAdapter = BookAdapter(availableBooks, object: CustomItemClickListener {
            override fun onItemClick(v: View, position: Int) {
                val selectedBook = availableBooks[position]
                goToBookInfoActivity(selectedBook)
            }
        })

        loadAllAvailableBooks()

        recyclerViewBooks.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = bookAdapter
        }

    }

    private fun goToBookInfoActivity(selectedBook: Book) {
        val infoIntent = Intent(this, BookInfoActivity::class.java)
        infoIntent.putExtra(EXTRA_SELECTED_BOOK, selectedBook)
        startActivity(infoIntent)
    }

    private fun loadAllAvailableBooks() {
        val publicBookPath = "$BOOKDOC_PATH"
        mFireStore?.collection(publicBookPath)?.get()?.addOnSuccessListener {
            availableBooks.clear()
            for (bookSnapShot in it) {
                Log.d(TAG, bookSnapShot.toString())
                val book =  bookSnapShot.toObject(Book::class.java)
                book.id = bookSnapShot.id
                availableBooks.add(book)
            }
            val bookAdapter = recyclerViewBooks.adapter
            bookAdapter?.notifyDataSetChanged()

        }?.addOnFailureListener {
            Log.d(TAG, it.toString())
        }

    }

    private fun setAccountHeaderInfo(userEmail: String) {
        mFireStore?.collection("$ACCOUNTDOC_PATH/$userEmail")?.document(USER_INFO)?.get()
            ?.addOnSuccessListener {
                val userInfo = it.toObject(User::class.java)
                if (userInfo == null) {
                    return@addOnSuccessListener
                }
                val userName = "${userInfo.firstName} ${userInfo.lastName}"
                val headerView = nav_view.getHeaderView(0)
                val emailView = headerView.findViewById<TextView>(R.id.id_nav_email)
                emailView.text = userEmail
                val nameView = headerView.findViewById<TextView>(R.id.id_nav_account_name)
                nameView.text = userName
            }
            ?.addOnFailureListener {
                ToastMe.message(this, "Failed to get name of the account user!")
            }
    }

    private fun setNavMenuLoginLogoutVisibility(currentUser: FirebaseUser?) {
        if (UserAccess.isLoggedIn(currentUser)) {
            navMenuForLoggedInUser()
        } else {
            navMenuForPublicUser()
        }
    }

    private fun navMenuForLoggedInUser() {
        val navMenu = findViewById<NavigationView>(R.id.nav_view).menu
        navMenu.findItem(R.id.nav_listing).isVisible = true
        navMenu.findItem(R.id.nav_pending).isVisible = true
        navMenu.findItem(R.id.nav_history).isVisible = true
        navMenu.findItem(R.id.nav_logout).isVisible = true
        navMenu.findItem(R.id.nav_accountInfo).isVisible = true
        navMenu.findItem(R.id.nav_login).isVisible = false
        navMenu.findItem(R.id.nav_sign_up).isVisible = false
    }

    private fun navMenuForPublicUser() {
        val navMenu = findViewById<NavigationView>(R.id.nav_view).menu
        navMenu.findItem(R.id.nav_listing).isVisible = false
        navMenu.findItem(R.id.nav_pending).isVisible = false
        navMenu.findItem(R.id.nav_history).isVisible = false
        navMenu.findItem(R.id.nav_logout).isVisible = false
        navMenu.findItem(R.id.nav_accountInfo).isVisible = false
        navMenu.findItem(R.id.nav_login).isVisible = true
        navMenu.findItem(R.id.nav_sign_up).isVisible = true
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
                startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.nav_search -> {
                startActivity(Intent(this, BookSearchActivity::class.java))
            }
            R.id.nav_listing -> {
                startActivity(Intent(this, ListingActivity::class.java))
            }
            R.id.nav_pending -> {

            }
            R.id.nav_history -> {

            }
            R.id.nav_sign_up -> {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
            R.id.nav_login -> {
                if (!UserAccess.isLoggedIn(currentUser)) {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            R.id.nav_accountInfo -> {
                if (UserAccess.isLoggedIn(currentUser)) {
                    val email = currentUser?.email
                    if(email == null) return false
                    editAccountInfo(email)
                }
            }
            R.id.nav_logout -> {
                if (UserAccess.isLoggedIn(currentUser)) {
                    mAuth?.signOut()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun editAccountInfo(userEmail: String) {
        mFireStore?.collection("$ACCOUNTDOC_PATH/$userEmail")?.document(USER_INFO)?.get()
            ?.addOnSuccessListener {
                val userInfo = it.toObject(User::class.java)
                if (userInfo == null) {
                    return@addOnSuccessListener
                }

                val intent = Intent(this, AccountInfoActivity::class.java)
                intent.putExtra(USER_INFO, userInfo)
                startActivityForResult(intent, REQ_CODE_EDIT_ACCOUNT_INFO)
            }
            ?.addOnFailureListener {
                ToastMe.message(this, "Failed to get account info!")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        if(requestCode == REQ_CODE_EDIT_ACCOUNT_INFO) {
            val updatedAccountInfo = data.getParcelableExtra<User>(UPDATED_USER_INFO)

            if (updatedAccountInfo == null) return

            val email = updatedAccountInfo.email
            val password = updatedAccountInfo.password

            mFireStore?.collection(email)?.document("User Info")?.set(updatedAccountInfo)
                ?.addOnCompleteListener {
                    ToastMe.message(this, "Account information updated successfully!")
                    setAccountHeaderInfo(email)
                }?.addOnFailureListener {
                    ToastMe.message(this, "Account information update failed!")
                    Log.d(TAG, it.toString())
                }

            val currentUser = mAuth?.currentUser
            currentUser?.updatePassword(password)
                ?.addOnSuccessListener {
                    ToastMe.message(this, "Password updated successfully!")
                }
                ?.addOnFailureListener {
                    ToastMe.message(this, "Password update failed!")
                }
        }
    }
}
