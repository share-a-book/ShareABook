package edu.uco.ychong.shareabook.user.tracking.fragments.pending

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.BookStatus
import edu.uco.ychong.shareabook.book.CustomItemClickListener
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.book.fragments.BOOKDOC_PATH
import edu.uco.ychong.shareabook.model.Book
import kotlinx.android.synthetic.main.fragment_pending.*
import kotlinx.android.synthetic.main.fragment_pending.view.*

class PendingFragment: Fragment() {

    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private val pendingRequestedBooks = ArrayList<Book>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()
        val inflatedView = inflater.inflate(R.layout.fragment_pending, container, false)

        var viewManager = LinearLayoutManager(context)
        val bookAdapter = BookPendingBorrowerAdapter(pendingRequestedBooks, object : CustomItemClickListener {
            override fun onItemClick(v: View, position: Int) {}
        })

        loadPendingRequestedBook()

        inflatedView.id_pendingRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = bookAdapter
        }

        return inflatedView
    }


    private fun loadPendingRequestedBook() {
        mFireStore?.collection("$BOOKDOC_PATH")
                ?.whereEqualTo("status", BookStatus.REQUEST_PENDING)
                ?.get()
                ?.addOnSuccessListener {

                    pendingRequestedBooks.clear()

                    for (bookSnapShot in it) {
                        val book =  bookSnapShot.toObject(Book::class.java)
                        book.id = bookSnapShot.id
                        if (isMyRequestToOtherUser(book.borrower)) {
                            pendingRequestedBooks.add(book)
                        }
                    }
                    val bookAdapter = id_pendingRecyclerView.adapter
                    bookAdapter?.notifyDataSetChanged()

                }?.addOnFailureListener {
                    Log.d(TESTTAG, it.toString())
                }
    }

    private fun isMyRequestToOtherUser(borrower: String): Boolean {
        val userEmail = mAuth?.currentUser?.email
        return (borrower == userEmail)
    }
}