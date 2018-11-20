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
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.CustomItemClickListener
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.book.fragments.REQUESTDOC_PATH
import edu.uco.ychong.shareabook.model.Request
import edu.uco.ychong.shareabook.model.RequestStatus
import kotlinx.android.synthetic.main.fragment_pending.*
import kotlinx.android.synthetic.main.fragment_pending.view.*

class PendingFragment: Fragment() {

    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private val pendingRequestedBooks = ArrayList<Request>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()
        val inflatedView = inflater.inflate(R.layout.fragment_pending, container, false)

        var viewManager = LinearLayoutManager(context)
        val pendingAdapter = PendingAdapter(pendingRequestedBooks, object : CustomItemClickListener {
            override fun onItemClick(v: View, position: Int) {}
        })

        loadPendingRequests()

        inflatedView.id_pendingRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = pendingAdapter
        }
        return inflatedView
    }

    private fun loadPendingRequests() {
        val userEmail = mAuth?.currentUser?.email
        mFireStore?.collection("$REQUESTDOC_PATH")
                ?.whereEqualTo("requestStatus", RequestStatus.REQUEST_PENDING)
                ?.whereEqualTo("borrowerEmail", userEmail)
                ?.get()
                ?.addOnSuccessListener {
                    pendingRequestedBooks.clear()
                    loadPendingSnapshot(it)
                }?.addOnFailureListener {
                    Log.d(TESTTAG, it.toString())
                }
    }

    private fun loadPendingSnapshot(it: QuerySnapshot) {
        for (requestSnapshot in it) {
            val request =  requestSnapshot.toObject(Request::class.java)
            request.id = requestSnapshot.id
            Log.d(TESTTAG, request.toString())
            pendingRequestedBooks.add(request)
        }
        notifyAdapterDataChange()
    }

    private fun notifyAdapterDataChange() {
        val pendingAdapter = id_pendingRecyclerView.adapter
        pendingAdapter?.notifyDataSetChanged()
    }
}