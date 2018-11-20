package edu.uco.ychong.shareabook.user.tracking.fragments.pending

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.MainActivity
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.PLAIN_TEXT
import edu.uco.ychong.shareabook.book.fragments.BOOKDOC_PATH
import edu.uco.ychong.shareabook.book.fragments.REQUESTDOC_PATH
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.model.Book
import edu.uco.ychong.shareabook.model.Request
import edu.uco.ychong.shareabook.model.RequestStatus
import kotlinx.android.synthetic.main.activity_cancel_book_request.*
import kotlinx.android.synthetic.main.book_card_layout.*

const val CODE_CANCEL_REQUEST_EMAIL = 2

class CancelBookRequestActivity : AppCompatActivity() {

    private var mFireStore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancel_book_request)

        mFireStore = FirebaseFirestore.getInstance()

        val theBook = intent.getParcelableExtra<Request>(CANCEL_BOOK_REQUEST)
        val requestId = intent.getStringExtra(CANCEL_BOOK_REQUEST_ID)

        val ownerEmail = theBook.lenderEmail
        val title = theBook.bookTitle
        val author = theBook.bookAuthor

        retrieveBookInfo(theBook)

        cancelBookButton.setOnClickListener {
            showCancelAlertDialog(requestId, ownerEmail, title, author)
        }
    }

    private fun retrieveBookInfo(theBook: Request) {
        mFireStore?.collection(BOOKDOC_PATH)?.document(theBook.bookId)?.get()
            ?.addOnSuccessListener {
                val book = it.toObject(Book::class.java)

                if(book == null) return@addOnSuccessListener

                id_cancelBookInfoImage.setImageResource(R.drawable.emptyphoto)
                id_cancelBookInfoTitle.text = theBook.bookTitle
                id_cancelBookInfoAuthor.text = theBook.bookAuthor
                info_cancelBookDatePosted.text = book.datePosted

                val bookStatus = theBook.requestStatus
                if(bookStatus == RequestStatus.REQUEST_PENDING) {
                    id_cancelBookInfoStatus.text = "Waiting for owner response..."
                }

                info_cancelBookInfoDescription.text = "Description:\n${book.description}"
                info_cancelBookPostedBy.text = "Posted by: ${book.lenderName}"
            }
    }

    private fun showCancelAlertDialog(requestId: String, email: String, title: String, author: String) {
        val builder = AlertDialog.Builder(this@CancelBookRequestActivity)
        builder.setTitle("Cancel Book Request")
        builder.setMessage("Are you sure you want to cancel this book request?")
        builder.setPositiveButton("YES") { dialog, which ->
            cancelBook(requestId, email, title, author)
        }.setNegativeButton("NO") { dialog, which ->
        }.setNeutralButton("CANCEL") { dialog, which ->
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GRAY)
    }

    private fun cancelBook(requestId: String, email: String, title: String, author: String) {
        mFireStore?.collection(REQUESTDOC_PATH)?.document(requestId)
            ?.update("requestStatus", RequestStatus.REQUEST_CANCELED)
            ?.addOnSuccessListener {
                sendCancelBookRequestToOwnerEmail(
                    email,
                    "Share-A-Book Book Request Cancellation: $title",
                    "The borrower has cancelled the request to borrow $title by $author."
                )
            }
            ?.addOnFailureListener {
                ToastMe.message(this, "Cancel request email failed to send.")
            }
    }

    private fun sendCancelBookRequestToOwnerEmail(ownerEmail: String, subject: String, message: String) {
        val sendEmailIntent = Intent(Intent.ACTION_SEND)
        sendEmailIntent.type = PLAIN_TEXT
        sendEmailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(ownerEmail))
        sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sendEmailIntent.putExtra(Intent.EXTRA_TEXT,message)
        startActivityForResult(Intent.createChooser(sendEmailIntent, "Sending email..."), CODE_CANCEL_REQUEST_EMAIL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CODE_CANCEL_REQUEST_EMAIL) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
