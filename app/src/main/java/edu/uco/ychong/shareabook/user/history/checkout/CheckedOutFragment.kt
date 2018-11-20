package edu.uco.ychong.shareabook.user.history.checkout

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.book.fragments.HISTORYDOC_PATH
import edu.uco.ychong.shareabook.model.Book
import edu.uco.ychong.shareabook.model.History
import edu.uco.ychong.shareabook.model.HistoryStatus
import kotlinx.android.synthetic.main.fragment_checkedout.*
import kotlinx.android.synthetic.main.fragment_checkedout.view.*

class CheckedOutFragment: Fragment() {
    private var mAuth: FirebaseAuth? = null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private val checkedOutBooks = ArrayList<History>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()

        val inflatedView = inflater.inflate(R.layout.fragment_checkedout, container, false)
        Log.d(TESTTAG, "checked out fragment")
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
}