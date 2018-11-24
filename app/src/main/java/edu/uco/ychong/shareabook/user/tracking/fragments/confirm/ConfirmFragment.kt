package edu.uco.ychong.shareabook.user.tracking.fragments.confirm

import android.content.Context
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
import edu.uco.ychong.shareabook.MainActivity
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.book.fragments.BOOKDOC_PATH
import edu.uco.ychong.shareabook.book.fragments.HISTORYDOC_PATH
import edu.uco.ychong.shareabook.book.fragments.REQUESTDOC_PATH
import edu.uco.ychong.shareabook.helper.DateManager
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.model.*
import kotlinx.android.synthetic.main.fragment_confirm.*
import kotlinx.android.synthetic.main.fragment_confirm.view.*

class ConfirmFragment: Fragment() {

    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private val acceptedRequestedBooks = ArrayList<Request>()
    private var parentContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()

        val inflatedView = inflater.inflate(R.layout.fragment_confirm, container, false)
        Log.d(TESTTAG, "confirm fragment")
        var viewManager = LinearLayoutManager(context)
        val confirmAdapter = ConfirmAdapter(acceptedRequestedBooks, this)

        loadConfirmedRequests()

        inflatedView.id_confirmRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = confirmAdapter
        }

        return inflatedView
    }


    private fun loadConfirmedRequests() {
        val userEmail = mAuth?.currentUser?.email
        mFireStore?.collection("$REQUESTDOC_PATH")
            ?.whereEqualTo("requestStatus", RequestStatus.REQUEST_ACCEPTED)
            ?.whereEqualTo("borrowerEmail",userEmail)
            ?.get()
            ?.addOnSuccessListener {
                acceptedRequestedBooks.clear()
                loadConfirmedSnapshot(it)
                notifyAdapterDataChange()
            }
    }

    private fun loadConfirmedSnapshot(it: QuerySnapshot) {
        for (requestSnapshot in it) {
            val request =  requestSnapshot.toObject(Request::class.java)
            request.id = requestSnapshot.id
            acceptedRequestedBooks.add(request)
        }
    }

    private fun notifyAdapterDataChange() {
        val confirmAdapter = id_confirmRecyclerView.adapter
        confirmAdapter?.notifyDataSetChanged()
    }

    fun checkout(request: Request) {
        mFireStore?.collection(REQUESTDOC_PATH)?.document(request.id)
            ?.update("requestStatus", HistoryStatus.CHECKED_OUT)
            ?.addOnSuccessListener {
                mFireStore?.collection(BOOKDOC_PATH)?.document(request.bookId)?.get()
                    ?.addOnSuccessListener {
                        val bookInfo = it.toObject(Book::class.java) ?: return@addOnSuccessListener
                        val lenderName = bookInfo.lenderName
                        val history = History(request.bookTitle,
                            request.bookAuthor,
                            request.bookImageUrl,
                            request.lenderEmail,
                            lenderName,
                            request.borrowerEmail,
                            MainActivity.userFullName,
                            HistoryStatus.CHECKED_OUT,
                            DateManager.getCurrentDateWithFullFormat())
                        mFireStore?.collection(HISTORYDOC_PATH)?.document()?.set(history)
                            ?.addOnSuccessListener {
                                if (parentContext != null)
                                    ToastMe.message(parentContext as Context, "Checkout successful")
                                loadConfirmedRequests()
                            }

                    }

                val parentContext = activity?.applicationContext
                if (parentContext != null)
                    ToastMe.message(parentContext, "Checked Out")
            }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentContext = context
    }
}