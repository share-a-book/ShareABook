package edu.uco.ychong.shareabook.user.history.history

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.fragments.HISTORYDOC_PATH
import edu.uco.ychong.shareabook.model.History
import edu.uco.ychong.shareabook.model.HistoryStatus
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_history.view.*

class HistoryFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private val histories = ArrayList<History>()
    private var parentContext: Context?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()

        val inflatedView = inflater.inflate(R.layout.fragment_history, container, false)
        var viewManager = LinearLayoutManager(context)
        val returnAdapter = HistoryAdapter(histories, this)

        val userEmail = mAuth?.currentUser?.email

        if (userEmail != null) {
            loadHistories(userEmail)
        }

        inflatedView.id_historyRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = returnAdapter
        }

        return inflatedView
    }


    private fun loadHistories(userEmail: String) {
        mFireStore?.collection(HISTORYDOC_PATH)
                ?.whereEqualTo("lenderEmail", userEmail)
                ?.whereEqualTo("historyStatus", HistoryStatus.RETURN_COMPLETED)
                ?.get()
                ?.addOnSuccessListener {
                    histories.clear()
                    loadHistorySnapshot(it)
                    notifyAdapterDataChange()
                }

        mFireStore?.collection(HISTORYDOC_PATH)
                ?.whereEqualTo("borrowerEmail", userEmail)
                ?.whereEqualTo("historyStatus", HistoryStatus.RETURN_COMPLETED)
                ?.get()
                ?.addOnSuccessListener {
                    loadHistorySnapshot(it)
                    notifyAdapterDataChange()
                }
    }

    private fun loadHistorySnapshot(it: QuerySnapshot) {
        for (returningBooks in it) {
            val book = returningBooks.toObject(History::class.java)
            book.id = returningBooks.id
            this.histories.add(book)
        }
    }

    private fun notifyAdapterDataChange() {
        val historyAdapter = id_historyRecyclerView.adapter
        historyAdapter?.notifyDataSetChanged()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentContext = context
    }

}
