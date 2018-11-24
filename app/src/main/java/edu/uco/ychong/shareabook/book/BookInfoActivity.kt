package edu.uco.ychong.shareabook.book

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import edu.uco.ychong.shareabook.EXTRA_SELECTED_BOOK
import edu.uco.ychong.shareabook.EXTRA_SELECTED_BOOK_ID
import edu.uco.ychong.shareabook.MainActivity
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.fragments.REQUESTDOC_PATH
import edu.uco.ychong.shareabook.helper.DateManager
import edu.uco.ychong.shareabook.helper.UserAccess
import edu.uco.ychong.shareabook.model.Book
import edu.uco.ychong.shareabook.model.Request
import edu.uco.ychong.shareabook.model.RequestStatus
import kotlinx.android.synthetic.main.activity_book_info.*

const val TESTTAG = "testtag"
const val CODE_EMAIL_SEND = 1
const val PLAIN_TEXT = "plain/text"

class BookInfoActivity : Activity() {
    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_info)
        id_bookImage.setImageResource(R.drawable.emptyphoto)
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()

        populateBookInformation()

        val selectedBook = intent.getParcelableExtra<Book>(EXTRA_SELECTED_BOOK)
        val selectedBookId = intent.getStringExtra(EXTRA_SELECTED_BOOK_ID)
        val lenderEmail = selectedBook.lenderEmail

        initializeRequestButtonVisibility()

        id_requestButton.setOnClickListener {

            Log.d(TESTTAG, "[BookInfoActivity]: selectedBook = ${selectedBook.id}")
            Log.d(TESTTAG, "[BookInfoActivity]: selectedBookId = ${selectedBookId}")
            sendBookRequestToLender(selectedBook, selectedBookId)

            if (id_emailLender.isChecked) {
                sendBookRequestToOwnerEmail(lenderEmail, selectedBook)
            }
        }
    }

    private fun populateBookInformation() {
        val selectedBookFromExtra = intent.getParcelableExtra<Book>(EXTRA_SELECTED_BOOK)
        id_infoBookTitle.text = selectedBookFromExtra.title
        id_infoBookAuthor.text = selectedBookFromExtra.author
        info_bookDatePosted.text = selectedBookFromExtra.datePosted
        id_infoBookStatus.text = "Status: ${selectedBookFromExtra.status}"
        id_bookDescription.text = selectedBookFromExtra.description
        id_bookPostedBy.text = "Posted by: ${selectedBookFromExtra.lenderName}"
        Log.d(TESTTAG, "[BookInfoActivity]: selectedBookFromExtra = ${selectedBookFromExtra.imageUrl}")

        if (!selectedBookFromExtra.imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(selectedBookFromExtra.imageUrl)
                .into(id_bookImage)
        }

    }

    private fun initializeRequestButtonVisibility() {
        val currentUser = mAuth?.currentUser
        val selectedBookFromExtra = intent.getParcelableExtra<Book>(EXTRA_SELECTED_BOOK)
        val lenderEmail = selectedBookFromExtra.lenderEmail

        if (!UserAccess.isLoggedIn(currentUser) || currentUser?.email == lenderEmail) {
            id_requestButton.visibility = View.GONE
            id_emailLender.visibility = View.GONE
        }
        else {
            id_requestButton.visibility = View.VISIBLE
            id_emailLender.visibility = View.VISIBLE
        }
    }

    @SuppressLint("LogConditional")
    private fun sendBookRequestToLender(bookInfo: Book, bookId: String) {
        Log.d(TESTTAG, "[BookInfoActivity]: bookInfo = $bookId")
        val userEmail = mAuth?.currentUser?.email
        if (userEmail != null) {
            val borrowRequest = Request(
                    bookId,
                    bookInfo.title,
                    bookInfo.author,
                    bookInfo.imageUrl,
                    bookInfo.lenderEmail,
                    userEmail,
                    "${MainActivity.userFullName}",
                    RequestStatus.REQUEST_PENDING,
                    DateManager.getCurrentDateWithFullFormat()
            )
            sendBookRequestToLender(borrowRequest)
        }
    }

    private fun sendBookRequestToLender(request: Request) {
        mFireStore?.collection(REQUESTDOC_PATH)?.document()?.set(request)
            ?.addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
            }
            ?.addOnFailureListener {
            }
    }

    private fun sendBookRequestToOwnerEmail(ownerEmail: String, book: Book) {
        val sendEmailIntent = Intent(Intent.ACTION_SEND)
        sendEmailIntent.type = PLAIN_TEXT
        sendEmailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(ownerEmail))
        sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "Share-A-Book Request: ${book.title}")
        sendEmailIntent.putExtra(Intent.EXTRA_TEXT, "Request to borrow ${book.title} by ${book.author}")
        startActivityForResult(Intent.createChooser(sendEmailIntent, "Sending email..."), CODE_EMAIL_SEND)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CODE_EMAIL_SEND) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}
