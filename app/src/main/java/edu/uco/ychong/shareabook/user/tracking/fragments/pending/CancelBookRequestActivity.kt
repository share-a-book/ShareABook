package edu.uco.ychong.shareabook.user.tracking.fragments.pending

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.MainActivity
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.BookStatus
import edu.uco.ychong.shareabook.book.PLAIN_TEXT
import edu.uco.ychong.shareabook.book.fragments.REQUESTDOC_PATH
import edu.uco.ychong.shareabook.book.fragments.BOOKDOC_PATH
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.model.Book
import kotlinx.android.synthetic.main.activity_cancel_book_request.*

const val CODE_CANCEL_REQUEST_EMAIL = 2

class CancelBookRequestActivity : AppCompatActivity() {

    private var mFireStore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancel_book_request)

        mFireStore = FirebaseFirestore.getInstance()

        val theBook = intent.getParcelableExtra<Book>(CANCEL_BOOK_REQUEST)
        val ownerEmail = theBook.lenderEmail
        val bookId = theBook.id
        val title = theBook.title
        val author = theBook.author

        retrieveBookInfo(theBook)

        cancelBookButton.setOnClickListener {
            showCancelAlertDialog(bookId, ownerEmail, title, author)
        }
    }

    private fun retrieveBookInfo(theBook: Book) {
        id_cancelBookInfoImage.setImageResource(R.drawable.emptyphoto)
        id_cancelBookInfoTitle.text = theBook.title
        id_cancelBookInfoAuthor.text = theBook.author
        info_cancelBookDatePosted.text = theBook.datePosted

        val bookStatus = theBook.status
        if(bookStatus == BookStatus.REQUEST_PENDING) {
            id_cancelBookInfoStatus.text = "Waiting for owner response..."
        }

        info_cancelBookInfoDescription.text = "Description:\n${theBook.description}"
        info_cancelBookPostedBy.text = "Posted by: ${theBook.lender}"
    }

    private fun showCancelAlertDialog(bookId: String, email: String, title: String, author: String) {
        val builder = AlertDialog.Builder(this@CancelBookRequestActivity)
        builder.setTitle("Cancel Book Request")
        builder.setMessage("Are you sure you want to cancel this book request?")
        builder.setPositiveButton("YES") { dialog, which ->
            getBorrowRequestId(bookId, email, title, author)
        }.setNegativeButton("NO") { dialog, which ->
        }.setNeutralButton("CANCEL") { dialog, which ->
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GRAY)
    }

    private fun getBorrowRequestId(bookId: String, email: String, title: String, author: String) {
        mFireStore?.collection("$BOOKDOC_PATH")?.document(bookId)
            ?.collection("$REQUESTDOC_PATH")
            ?.whereEqualTo("borrowStatus", BookStatus.REQUEST_PENDING)
            ?.get()
            ?.addOnSuccessListener {
                for (requestSnapShot in it) {
                    cancelBook(bookId, requestSnapShot.id, email, title, author)
                }
            }
            ?.addOnFailureListener {
            }

    }

    private fun cancelBook(bookId: String, requestId: String, email: String, title: String, author: String) {
        mFireStore?.collection(BOOKDOC_PATH)?.document(bookId)
            ?.collection(REQUESTDOC_PATH)?.document(requestId)
            ?.update("borrowStatus", BookStatus.CANCELLED)
            ?.addOnSuccessListener {
            }
            ?.addOnFailureListener {
            }

        mFireStore?.collection("$BOOKDOC_PATH")?.document(bookId)
            ?.update("status", BookStatus.AVAILABLE, "borrower", "")
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
