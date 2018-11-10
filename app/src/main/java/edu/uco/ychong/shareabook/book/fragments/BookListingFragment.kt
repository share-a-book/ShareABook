package edu.uco.ychong.shareabook.book.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.EXTRA_SELECTED_BOOK
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.BookAdapter
import edu.uco.ychong.shareabook.book.BookEditActivity
import edu.uco.ychong.shareabook.book.CustomItemClickListener
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.model.Book
import kotlinx.android.synthetic.main.fragment_book_listing.*
import kotlinx.android.synthetic.main.fragment_book_listing.view.*

class BookListingFragment : Fragment() {
    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    var allUserOwnedBooks = ArrayList<Book>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        val inflatedView = inflater.inflate(R.layout.fragment_book_listing, container, false)

        var viewManager = LinearLayoutManager(context)

        val bookAdapter = BookAdapter(allUserOwnedBooks, object : CustomItemClickListener {
            override fun onItemClick(v: View, position: Int) {
                val selectedBook = allUserOwnedBooks[position]
                goToBookEditActivity(selectedBook)
            }
        })

        loadUserOwnedBooks()

        inflatedView.recyclerViewUserOwnedBooks.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = bookAdapter
        }

        return inflatedView
    }

    private fun goToBookEditActivity(selectedBook: Book) {
        val infoIntent = Intent(context, BookEditActivity::class.java)
        infoIntent.putExtra(EXTRA_SELECTED_BOOK, selectedBook)
        startActivity(infoIntent)
    }


    private fun loadUserOwnedBooks() {
        val userEmail = mAuth?.currentUser?.email

        mFireStore?.collection("$BOOKDOC_PATH")
                ?.whereEqualTo("lenderEmail", userEmail)
                ?.get()
                ?.addOnSuccessListener {

            allUserOwnedBooks.clear()

            for (bookSnapShot in it) {
                val book =  bookSnapShot.toObject(Book::class.java)
                book.id = bookSnapShot.id
                allUserOwnedBooks.add(book)
            }
            val bookAdapter = recyclerViewUserOwnedBooks.adapter
            bookAdapter?.notifyDataSetChanged()

        }?.addOnFailureListener {
            Log.d(TESTTAG, it.toString())
        }
    }
}
