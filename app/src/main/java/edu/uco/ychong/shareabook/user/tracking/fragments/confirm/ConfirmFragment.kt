package edu.uco.ychong.shareabook.user.tracking.fragments.confirm

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
import edu.uco.ychong.shareabook.book.fragments.REQUESTDOC_PATH
import edu.uco.ychong.shareabook.model.Request
import edu.uco.ychong.shareabook.model.RequestStatus
import kotlinx.android.synthetic.main.fragment_confirm.*
import kotlinx.android.synthetic.main.fragment_confirm.view.*

class ConfirmFragment: Fragment() {

    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private val acceptedRequestedBooks = ArrayList<Request>()



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

    //don't make it private, the ConfirmAdapter need to call this function
    fun checkout(request: Request) {
        Log.d(TESTTAG, "(checkout) $request")

        /**Implement confirmation here***/
         /**
          * Create a history object and set it to the database.
          * Use HISTORYDOC_PATH as the path.
          * There's no adapter for this fragment yet,
          * you can make one if you want.
          * Or you can just present database that it shows a history document
          * was made after the user checked out.
          * **/
//         val history = History(var historyId: String,
//                       var bookTitle: String,
//                       var bookAuthor: String,
//                       var bookImageUrl: String,
//                       var lenderEmail: String,
//                       var lenderName: String,
//                       var borrowerEmail: String,
//                       MainActivity.userFullName,  <-- use this so you don't have to make another db call to get user name
//                       var historyStatus: String,
//                       var lastUpdatedDate: String)


    }
}