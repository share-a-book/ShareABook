package edu.uco.ychong.shareabook.book

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.EXTRA_SELECTED_BOOK
import edu.uco.ychong.shareabook.MainActivity
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.fragments.BOOKDOC_PATH
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.helper.UserAccess
import edu.uco.ychong.shareabook.model.Book
import kotlinx.android.synthetic.main.activity_book_info.*


const val TESTTAG = "testtag"
const val CODE_EMAIL_SEND = 1

class BookInfoActivity : Activity() {
    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    private val PLAIN_TEXT = "plain/text"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_info)
        id_infoBookImage.setImageResource(R.drawable.emptyphoto)
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()

        populateBookInformation()

        val selectedBookFromExtra = intent.getParcelableExtra<Book>(EXTRA_SELECTED_BOOK)
        val ownerEmail = selectedBookFromExtra.lenderEmail
        val bookId = selectedBookFromExtra.id

        initializeRequestButtonVisibility()

        requestButton.setOnClickListener {
            sendBookRequestToOwner(bookId)
            sendBookRequestToOwnerEmail(
                ownerEmail,
                "Share-A-Book Request: ${selectedBookFromExtra.title}",
                "Request to borrow ${selectedBookFromExtra.title} by ${selectedBookFromExtra.author}"
            )
        }

    }

    private fun populateBookInformation() {
        val selectedBookFromExtra = intent.getParcelableExtra<Book>(EXTRA_SELECTED_BOOK)
        id_infoBookTitle.text = selectedBookFromExtra.title
        id_infoBookAuthor.text = selectedBookFromExtra.author
        info_bookDatePosted.text = selectedBookFromExtra.datePosted
        id_infoBookStatus.text = "Status: ${selectedBookFromExtra.status}"
        info_bookDescription.text = selectedBookFromExtra.description
        info_bookPostedBy.text = "Posted by: ${selectedBookFromExtra.lender}"
    }

    private fun initializeRequestButtonVisibility() {
        val currentUser = mAuth?.currentUser
        val selectedBookFromExtra = intent.getParcelableExtra<Book>(EXTRA_SELECTED_BOOK)
        val lenderEmail = selectedBookFromExtra.lenderEmail

        if (!UserAccess.isLoggedIn(currentUser) || currentUser?.email == lenderEmail)
            requestButton.visibility = View.GONE
        else
            requestButton.visibility = View.VISIBLE
    }

    private fun sendBookRequestToOwner(bookId: String) {
        val userEmail = mAuth?.currentUser?.email

        mFireStore?.collection(BOOKDOC_PATH)
                ?.document(bookId)
                ?.update(
                    "status", BookStatus.REQUEST_PENDING,
                    "borrower", userEmail)
                ?.addOnSuccessListener {
                    ToastMe.message(this, "Request pending successfully.")
                }
                ?.addOnFailureListener {
                    ToastMe.message(this, "Request pending failed.")
                }
    }

    private fun sendBookRequestToOwnerEmail(ownerEmail: String, subject: String, message: String) {
        val sendEmailIntent = Intent(Intent.ACTION_SEND)
        sendEmailIntent.type = PLAIN_TEXT
        sendEmailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(ownerEmail))
        sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sendEmailIntent.putExtra(Intent.EXTRA_TEXT,message)
        startActivityForResult(Intent.createChooser(sendEmailIntent, "Sending email..."), CODE_EMAIL_SEND)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CODE_EMAIL_SEND) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}
