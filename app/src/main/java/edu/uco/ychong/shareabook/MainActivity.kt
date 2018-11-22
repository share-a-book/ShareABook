package edu.uco.ychong.shareabook

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import edu.uco.ychong.shareabook.book.BookSearchActivity
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.book.fragments.BOOKDOC_PATH
import edu.uco.ychong.shareabook.book.fragments.REQUESTDOC_PATH
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.helper.UserAccess
import edu.uco.ychong.shareabook.model.Upload
import edu.uco.ychong.shareabook.model.User
import edu.uco.ychong.shareabook.user.*
import edu.uco.ychong.shareabook.user.tracking.HistoryActivity
import edu.uco.ychong.shareabook.user.tracking.TrackingActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

const val USER_INFO = "user_info"
const val USER_PROFILE = "user_profile"
const val UPDATED_USER_INFO = "updated_user_info"
const val REQ_CODE_EDIT_ACCOUNT_INFO = 1
const val EXTRA_SELECTED_BOOK = "extra_selected_book"
const val EXTRA_SELECTED_BOOK_ID = "extra_selected_book_id"
const val EXTRA_ADD_BOOK_FRAGMENT = "extra_add_book_fragment"
const val EXTRA_TRACKING_TAB = "extra_tracking_tab"
const val EXTRA_HISTORY_TAB = "extra_history_tab"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mAuth: FirebaseAuth? = null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null

    companion object {
        var profileUrl: String = ""
        var userFullName: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()
        val currentUser = mAuth?.currentUser

        if (UserAccess.isLoggedIn(currentUser)) {
            val email = currentUser?.email ?: return
            setAccountHeaderInfo(email)
            loadProfileImage()
        }

        setNavMenuLoginLogoutVisibility(currentUser)
        handleHomeNavigation()
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TESTTAG, "onResume()")
        if (!profileUrl.isNullOrEmpty())
            loadProfileImage()
    }

    private fun handleHomeNavigation() {
        id_searchIcon.setOnClickListener {
            startActivity(Intent(this, BookSearchActivity::class.java))
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
        }

        id_addBookIcon.setOnClickListener {
            val intent = Intent(this, ListingActivity::class.java)
            intent.putExtra(EXTRA_ADD_BOOK_FRAGMENT, "addBook")
            startActivity(intent)
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        }

        id_checkoutIcon.setOnClickListener {
            val intent = Intent(this, TrackingActivity::class.java)
            intent.putExtra(EXTRA_TRACKING_TAB, "confirmed")
            startActivity(intent)
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
        }

        id_requestIcon.setOnClickListener {
            val intent = Intent(this, TrackingActivity::class.java)
            intent.putExtra(EXTRA_TRACKING_TAB, "request")
            startActivity(intent)
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        }

        id_historyIcon.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra(EXTRA_HISTORY_TAB, "returned")
            startActivity(intent)
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
        }

        id_pendingIcon.setOnClickListener {
            val intent = Intent(this, TrackingActivity::class.java)
            intent.putExtra(EXTRA_TRACKING_TAB, "pending")
            startActivity(intent)
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        }

        id_settingsIcon.setOnClickListener {
            val email = mAuth?.currentUser?.email
            if (email != null) {
                editAccountInfo(email)
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
            }
        }

        id_returnBookIcon.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra(EXTRA_HISTORY_TAB, "checked_out")
            startActivity(intent)
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        }


    }

    private fun setAccountHeaderInfo(userEmail: String) {
        mFireStore?.collection("$ACCOUNT_DOC_PATH/$userEmail")
            ?.document(USER_INFO)
            ?.get()
            ?.addOnSuccessListener {
                val userInfo = it.toObject(User::class.java) ?: return@addOnSuccessListener
                val userName = "${userInfo.firstName} ${userInfo.lastName}"
                userFullName = userName
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

    private fun loadProfileImage() {
        val userEmail = mAuth?.currentUser?.email
        mFireStore?.collection("$PROFILE_STORAGE_PATH/$userEmail")
            ?.document(USER_PROFILE)
            ?.get()
            ?.addOnSuccessListener {
                val userProfileImage = it.toObject(Upload::class.java)
                if (userProfileImage != null) {
                    Log.d(TESTTAG, "got url: ${userProfileImage.url}")
                    profileUrl = userProfileImage.url
                    Picasso.get().load(userProfileImage.url).into(id_profile_image)
                }
            }
            ?.addOnFailureListener {
                Log.d(TESTTAG, it.toString())
            }
    }

    private fun setNavMenuLoginLogoutVisibility(currentUser: FirebaseUser?) {
        val userEmail = currentUser?.email
        if (userEmail != null)
            navMenuForLoggedInUser()
        else
            navMenuForPublicUser()
    }

    private fun navMenuForLoggedInUser() {
        val navMenu = findViewById<NavigationView>(R.id.nav_view).menu
        navMenu.findItem(R.id.nav_listing).isVisible = true
        navMenu.findItem(R.id.nav_tracking).isVisible = true
        navMenu.findItem(R.id.nav_history).isVisible = true
        navMenu.findItem(R.id.nav_logout).isVisible = true
        navMenu.findItem(R.id.nav_accountInfo).isVisible = true
        navMenu.findItem(R.id.nav_login).isVisible = false
        navMenu.findItem(R.id.nav_sign_up).isVisible = false

        if (!profileUrl.isNullOrEmpty() && id_profile_image != null)
            Picasso.get().load(profileUrl).noFade().into(id_profile_image)


    }

    private fun navMenuForPublicUser() {
        val navMenu = findViewById<NavigationView>(R.id.nav_view).menu
        navMenu.findItem(R.id.nav_home).isVisible = false
        navMenu.findItem(R.id.nav_listing).isVisible = false
        navMenu.findItem(R.id.nav_tracking).isVisible = false
        navMenu.findItem(R.id.nav_history).isVisible = false
        navMenu.findItem(R.id.nav_logout).isVisible = false
        navMenu.findItem(R.id.nav_accountInfo).isVisible = false
        navMenu.findItem(R.id.nav_login).isVisible = true
        navMenu.findItem(R.id.nav_sign_up).isVisible = true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
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
            R.id.nav_tracking -> {
                startActivity(Intent(this, TrackingActivity::class.java))
            }
            R.id.nav_history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
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
                    val email = currentUser?.email ?: return false
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
        mFireStore?.collection("$ACCOUNT_DOC_PATH/$userEmail")
            ?.document(USER_INFO)
            ?.get()
            ?.addOnSuccessListener {
                val userInfo = it.toObject(User::class.java) ?: return@addOnSuccessListener
                val intent = Intent(this, AccountInfoActivity::class.java)
                intent.putExtra(USER_INFO, userInfo)
                val userProfile = Upload(USER_PROFILE, profileUrl)
                intent.putExtra(USER_PROFILE, userProfile)
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
            val updatedAccountInfo = data.getParcelableExtra<User>(UPDATED_USER_INFO) ?: return
            val userEmail = updatedAccountInfo.email
            val password = updatedAccountInfo.password

            val currentUser = mAuth?.currentUser
            currentUser?.updatePassword(password)
                ?.addOnSuccessListener {
                    ToastMe.message(this, "Password updated successfully.")
                }
                ?.addOnFailureListener {
                    ToastMe.message(this, "Password updated failed.")
                }

            mFireStore?.collection("$ACCOUNT_DOC_PATH/$userEmail")?.document(USER_INFO)?.set(updatedAccountInfo)
                ?.addOnCompleteListener {
                    ToastMe.message(this, "Account information updated successfully!")
                    setAccountHeaderInfo(userEmail)
                }?.addOnFailureListener {
                    ToastMe.message(this, "Account information update failed!")
                }

            val userName = "${updatedAccountInfo.firstName} ${updatedAccountInfo.lastName}"
            val userPhoneNumber = updatedAccountInfo.phoneNumber
            updateLenderName(userEmail, userName)
            updateBorrowerName(userEmail, userName, userPhoneNumber)
        }
    }

    private fun updateLenderName(email: String, lenderName: String) {
        mFireStore?.collection("$BOOKDOC_PATH")
            ?.whereEqualTo("lenderEmail", email)
            ?.get()
            ?.addOnSuccessListener {
                for (bookSnapshot in it) {
                    mFireStore?.collection("$BOOKDOC_PATH")?.document(bookSnapshot.id)
                        ?.update("lenderName", lenderName)
                        ?.addOnSuccessListener {
                            ToastMe.message(this, "Update name successfully.")
                        }
                        ?.addOnFailureListener {
                            Log.d(TESTTAG, it.toString())
                        }
                }
            }
            ?.addOnFailureListener {
                ToastMe.message(this, "Failed to retrieve user.")
            }
    }

    private fun updateBorrowerName(email: String, borrowerName: String, borrowerNumber: String) {
        mFireStore?.collection("$BOOKDOC_PATH")?.get()?.addOnSuccessListener {
            for (bookSnapshot in it) {
                mFireStore?.collection("$BOOKDOC_PATH")?.document(bookSnapshot.id)
                    ?.collection("$REQUESTDOC_PATH")
                    ?.whereEqualTo("borrowerEmail", email)
                    ?.get()
                    ?.addOnSuccessListener {
                        for (requestSnapshot in it) {
                            mFireStore?.collection("$BOOKDOC_PATH")?.document(bookSnapshot.id)
                                ?.collection("$REQUESTDOC_PATH")?.document(requestSnapshot.id)
                                ?.update("borrowerName", borrowerName, "borrowerNumber", borrowerNumber)
                                ?.addOnSuccessListener {  }
                                ?.addOnFailureListener {  }
                        }
                    }
            }
        }
    }
}
