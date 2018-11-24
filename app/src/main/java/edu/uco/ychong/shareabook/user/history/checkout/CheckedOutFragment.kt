package edu.uco.ychong.shareabook.user.history.checkout

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
import kotlinx.android.synthetic.main.fragment_checkedout.*
import kotlinx.android.synthetic.main.fragment_checkedout.view.*

class CheckedOutFragment: Fragment() {
    private var mAuth: FirebaseAuth? = null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private val checkedOutBooks = ArrayList<History>()
    private var parentContext: Context ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()

        val inflatedView = inflater.inflate(R.layout.fragment_checkedout, container, false)
        var viewManager = LinearLayoutManager(context)
        val checkedOutAdapter = CheckedOutAdapter(checkedOutBooks, this)

        loadCheckedOutBooks()

        inflatedView.id_checkedOutRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = checkedOutAdapter
        }

        return inflatedView
    }

    private fun loadCheckedOutBooks() {
        val userEmail = mAuth?.currentUser?.email
        mFireStore?.collection(HISTORYDOC_PATH)
            ?.whereEqualTo("borrowerEmail", userEmail)
            ?.whereEqualTo("historyStatus", HistoryStatus.CHECKED_OUT)
            ?.get()
            ?.addOnSuccessListener {
                checkedOutBooks.clear()
                loadCheckedOutSnapshot(it)
                notifyAdapterDataChange()

            }
        mFireStore?.collection(HISTORYDOC_PATH)
                ?.whereEqualTo("borrowerEmail", userEmail)
                ?.whereEqualTo("historyStatus", HistoryStatus.RETURN_PROCESSING)
                ?.get()
                ?.addOnSuccessListener {
                    loadCheckedOutSnapshot(it)
                    notifyAdapterDataChange()

                }
    }

    private fun loadCheckedOutSnapshot(it: QuerySnapshot) {
        for (checkedBooks in it) {
            val book = checkedBooks.toObject(History::class.java)
            book.id = checkedBooks.id
            checkedOutBooks.add(book)
        }
    }

    private fun notifyAdapterDataChange() {
        val checkedOutAdapter = id_checkedOutRecyclerView.adapter
        checkedOutAdapter?.notifyDataSetChanged()
    }

    fun returnBook(position: Int) {
        val historyId = checkedOutBooks[position].id
        Log.d(TESTTAG, "[CheckedOutFragmnet]: historyId = $historyId")
        mFireStore?.collection(HISTORYDOC_PATH)
            ?.document(historyId)
            ?.update("historyStatus", HistoryStatus.RETURN_PROCESSING)
            ?.addOnSuccessListener {
                if (parentContext != null)
                    ToastMe.message(parentContext as Context,"Request to return success")
                loadCheckedOutBooks()
            }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentContext = context
    }
}