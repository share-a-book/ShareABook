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
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.book.fragments.BOOKDOC_PATH
import edu.uco.ychong.shareabook.book.fragments.REQUESTDOC_PATH
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.model.BookStatus
import edu.uco.ychong.shareabook.model.Request
import edu.uco.ychong.shareabook.model.RequestStatus
import kotlinx.android.synthetic.main.fragment_request_incoming.*
import kotlinx.android.synthetic.main.fragment_request_incoming.view.*

class RequestFragment: Fragment() {

    private var mAuth: FirebaseAuth? = null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private val requestedBooks = ArrayList<Request>()

    override fun onCreateView(inflater: LayoutInflater, container:
    ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()
        val inflatedView = inflater.inflate(R.layout.fragment_request_incoming, container, false)

        var viewManager = LinearLayoutManager(context)
        val requestAdapter = RequestAdapter(requestedBooks, this)

        loadRequests()

        inflatedView.id_requestIncomingRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = requestAdapter
        }

        return inflatedView
    }

    private fun loadRequests() {
        val userEmail = mAuth?.currentUser?.email
        if (userEmail != null)
        mFireStore?.collection("$REQUESTDOC_PATH")
            ?.whereEqualTo("lenderEmail", userEmail)
            ?.whereEqualTo("requestStatus", RequestStatus.REQUEST_PENDING)
            ?.get()
            ?.addOnSuccessListener {
                requestedBooks.clear()
                loadRequestSnapshot(it)
            }?.addOnFailureListener {
                Log.d(TESTTAG, it.toString())
            }

    }

    private fun loadRequestSnapshot(it: QuerySnapshot) {
        for (requestSnapshot in it) {
            val request =  requestSnapshot.toObject(Request::class.java)
            request.id = requestSnapshot.id
            requestedBooks.add(request)
        }
        notifyAdapterDataChange()
    }

    private fun notifyAdapterDataChange() {
        if (id_requestIncomingRecyclerView != null) {
            val bookAdapter = id_requestIncomingRecyclerView.adapter
            bookAdapter?.notifyDataSetChanged()
        }
    }

    fun acceptRequest(request: Request) {
        Log.d(TESTTAG, "accepted request: ${request.id}")
        mFireStore?.collection(REQUESTDOC_PATH)?.document(request.id)
            ?.update("requestStatus", RequestStatus.REQUEST_ACCEPTED)
            ?.addOnSuccessListener {
                val parentContext = activity?.applicationContext
                if (parentContext != null)
                    ToastMe.message(parentContext, "Accepted successful")
            }

        setBookStatusToUnavailable(request.bookId)
    }

    private fun setBookStatusToUnavailable(bookId: String) {
        Log.d(TESTTAG, "(setBookStatusToUnavailable) bookId = $bookId")
        mFireStore?.collection(BOOKDOC_PATH)?.document(bookId)
            ?.update("status", BookStatus.UNAVAILABLE)
            ?.addOnSuccessListener {
                val parentContext = activity?.applicationContext
                if (parentContext != null)
                    ToastMe.message(parentContext, "Book remove from public")
            }
    }

    fun rejectRequest(request: Request) {
        Log.d(TESTTAG, "reject request: ${request.id}")
        mFireStore?.collection(REQUESTDOC_PATH)?.document(request.id)
            ?.update("requestStatus", RequestStatus.REQUEST_REJECTED)
            ?.addOnSuccessListener {
                val parentContext = activity?.applicationContext
                if (parentContext != null)
                    ToastMe.message(parentContext, "Rejected request")
            }
    }
}