package edu.uco.ychong.shareabook.user.tracking.fragments.pending

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.MainActivity
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.BookStatus
import edu.uco.ychong.shareabook.book.fragments.BOOKDOC_PATH
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.model.Book
import kotlinx.android.synthetic.main.activity_cancel_book_request.*

class CancelBookRequestActivity : AppCompatActivity() {

    private var mFireStore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancel_book_request)

        mFireStore = FirebaseFirestore.getInstance()

        val theBook = intent.getParcelableExtra<Book>(CANCEL_BOOK_REQUEST)
        val bookId = theBook.id

        retrieveBookInfo(theBook)

        cancelBookButton.setOnClickListener {
            showCancelAlertDialog(bookId)
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

    private fun showCancelAlertDialog(bookId: String) {
        val builder = AlertDialog.Builder(this@CancelBookRequestActivity)
        builder.setTitle("Cancel Book Request")
        builder.setMessage("Are you sure you want to cancel this book request?")
        builder.setPositiveButton("YES") { dialog, which ->
            cancelBook(bookId)
        }.setNegativeButton("NO") { dialog, which ->
        }.setNeutralButton("CANCEL") { dialog, which ->
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GRAY)
    }

    private fun cancelBook(bookId: String) {
        mFireStore?.collection("$BOOKDOC_PATH")?.document(bookId)
            ?.update("status", BookStatus.AVAILABLE, "borrower", "")
            ?.addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
            }
            ?.addOnFailureListener {
            }
    }
}
