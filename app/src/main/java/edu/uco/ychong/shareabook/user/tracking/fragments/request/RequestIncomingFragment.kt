package edu.uco.ychong.shareabook.user.tracking.fragments.request

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
import edu.uco.ychong.shareabook.book.CustomItemClickListener
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.book.fragments.BOOKDOC_PATH
import edu.uco.ychong.shareabook.model.Book
import edu.uco.ychong.shareabook.model.BorrowRequest
import kotlinx.android.synthetic.main.fragment_request_incoming.*
import kotlinx.android.synthetic.main.fragment_request_incoming.view.*

class RequestIncomingFragment: Fragment() {

    private var mAuth: FirebaseAuth? = null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private val pendingRequestedBooks = ArrayList<Book>()
    private val allLenderBooks = ArrayList<Book>()
    private val allRequestsPending = ArrayList<BorrowRequest>()


    override fun onCreateView(inflater: LayoutInflater, container:
    ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()
        val inflatedView = inflater.inflate(R.layout.fragment_request_incoming, container, false)

        var viewManager = LinearLayoutManager(context)
        val bookAdapter = BookRequestAdapter(pendingRequestedBooks, object : CustomItemClickListener {
            override fun onItemClick(v: View, position: Int) {}
        })

        loadPendingRequestedBook()

        inflatedView.id_requestIncomingRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = bookAdapter
        }

        return inflatedView
    }

    private fun loadPendingRequestedBook() {
        val userEmail = mAuth?.currentUser?.email
        if (userEmail != null)
        mFireStore?.collection("$BOOKDOC_PATH")
            ?.whereEqualTo("lenderEmail", userEmail)
            ?.get()
            ?.addOnSuccessListener {
                allLenderBooks.clear()
                for (bookSnapShot in it) {
                    val book =  bookSnapShot.toObject(Book::class.java)
                    book.id = bookSnapShot.id
                    if (isOtherUserRequestToMe(book.lenderEmail))
                        allLenderBooks.add(book)
                }
                val bookAdapter = id_requestIncomingRecyclerView.adapter
                bookAdapter?.notifyDataSetChanged()

            }?.addOnFailureListener {
                Log.d(TESTTAG, it.toString())
            }

    }

    private fun isOtherUserRequestToMe(lender: String): Boolean {
        val userEmail = mAuth?.currentUser?.email
        return (lender == userEmail)
    }
}