package edu.uco.ychong.shareabook.user.history.returned

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
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.book.fragments.HISTORYDOC_PATH
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.model.History
import edu.uco.ychong.shareabook.model.HistoryStatus
import kotlinx.android.synthetic.main.fragment_returned.*
import kotlinx.android.synthetic.main.fragment_returned.view.*

class ReturnedFragment : Fragment() {
    private var mAuth: FirebaseAuth? = null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private val returningBooks = ArrayList<History>()
    private var parentContext: Context?= null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()

        val inflatedView = inflater.inflate(R.layout.fragment_returned, container, false)
        var viewManager = LinearLayoutManager(context)
        val returnAdapter = ReturnAdapter(returningBooks, this)

        loadReturningBooks()

        inflatedView.id_returningBooksRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = returnAdapter
        }

        return inflatedView
    }

    private fun loadReturningBooks() {
        val userEmail = mAuth?.currentUser?.email
        mFireStore?.collection(HISTORYDOC_PATH)
            ?.whereEqualTo("lenderEmail", userEmail)
            ?.whereEqualTo("historyStatus", HistoryStatus.RETURN_PROCESSING)
            ?.get()
            ?.addOnSuccessListener {
                returningBooks.clear()
                loadReturningBookSnapshot(it)
                notifyAdapterDataChange()

            }
    }

    private fun loadReturningBookSnapshot(it: QuerySnapshot) {
        for (returningBooks in it) {
            val book = returningBooks.toObject(History::class.java)
            book.id = returningBooks.id
            this.returningBooks.add(book)
        }
    }

    private fun notifyAdapterDataChange() {
        val returnAdapter = id_returningBooksRecyclerView.adapter
        returnAdapter?.notifyDataSetChanged()
    }

    fun confirmReturn(position: Int) {
        val historyId = returningBooks[position].id
        Log.d(TESTTAG, "[ReturnedFragment]: historyId = $historyId")
        mFireStore?.collection(HISTORYDOC_PATH)
            ?.document(historyId)
            ?.update("historyStatus", HistoryStatus.RETURN_COMPLETED)
            ?.addOnSuccessListener {
                if (parentContext != null)
                    ToastMe.message(parentContext as Context,"Confirm returned successfully")
                loadReturningBooks()
            }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentContext = context
    }
}
