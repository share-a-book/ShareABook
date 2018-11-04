package edu.uco.ychong.shareabook.book

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import edu.uco.ychong.shareabook.EXTRA_SELECTED_BOOK
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.helper.UserAccess
import edu.uco.ychong.shareabook.model.Book
import kotlinx.android.synthetic.main.activity_book_info.*


const val TESTTAG = "testtag"
class BookInfoActivity : Activity() {
    private var mAuth: FirebaseAuth?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_info)
        info_bookImage.setImageResource(R.drawable.emptyphoto)
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth?.currentUser

        val selectedBookFromExtra = intent.getParcelableExtra<Book>(EXTRA_SELECTED_BOOK)
        info_bookTitle.text = selectedBookFromExtra.title
        info_bookAuthor.text = selectedBookFromExtra.author
        info_bookDatePosted.text = selectedBookFromExtra.datePosted
        info_bookStatus.text = "Status: ${selectedBookFromExtra.status}"
        info_bookDescription.text = selectedBookFromExtra.description
        info_bookPostedBy.text = "Posted by: ${selectedBookFromExtra.lender}"
        val ownerEmail = selectedBookFromExtra.lenderEmail
        val requestButton = findViewById<Button>(R.id.requestButton)

        if (!UserAccess.isLoggedIn(currentUser)) {
            requestButton.visibility = View.GONE
            Log.d(TESTTAG, "user not log in")
        } else {
            requestButton.visibility = View.VISIBLE
            Log.d(TESTTAG, "user log in")
        }


        requestButton.setOnClickListener {
            sendBookRequestToOwnerEmail(ownerEmail, "Share-A-Book Request: ${selectedBookFromExtra.title}",
                "Request to borrow ${selectedBookFromExtra.title} by ${selectedBookFromExtra.author}")
        }

    }

    private fun sendBookRequestToOwnerEmail(ownerEmail: String, subject: String, message: String) {
        val sendEmailIntent = Intent(Intent.ACTION_SEND)
        sendEmailIntent.type = "plain/text"
        sendEmailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(ownerEmail))
        sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sendEmailIntent.putExtra(Intent.EXTRA_TEXT,message)
        startActivity(Intent.createChooser(sendEmailIntent, "Sending email..."))
    }
}
