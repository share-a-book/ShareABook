package edu.uco.ychong.shareabook.user.tracking.fragments.confirm

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import edu.uco.ychong.shareabook.USER_INFO
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.book.fragments.BOOKDOC_PATH
import edu.uco.ychong.shareabook.book.fragments.HISTORYDOC_PATH
import edu.uco.ychong.shareabook.book.fragments.REQUESTDOC_PATH
import edu.uco.ychong.shareabook.helper.DateManager
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.model.*
import edu.uco.ychong.shareabook.user.ACCOUNT_DOC_PATH
import kotlinx.android.synthetic.main.fragment_confirm.*
import kotlinx.android.synthetic.main.fragment_confirm.view.*

const val IS_CALL = true

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

    fun communicationPreference(context: Context, lenderEmail: String, bookTitle: String, bookAuthor: String, borrowerName: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Communication Preference")
        builder.setMessage("How do you want to contact the lender?")
        builder.setPositiveButton("TEXT") { dialog, which ->
            getPhoneNumber(lenderEmail, !IS_CALL, bookTitle, bookAuthor, borrowerName)
        }.setNegativeButton("CALL") { dialog, which ->
            getPhoneNumber(lenderEmail, IS_CALL, bookTitle, bookAuthor, borrowerName)
        }.setNeutralButton("CANCEL") { dialog, which ->
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GRAY)
    }

    private fun getPhoneNumber(lenderEmail: String, isCall: Boolean, bookTitle: String, bookAuthor: String, borrowerName: String) {
        mFireStore?.collection("$ACCOUNT_DOC_PATH/$lenderEmail")?.document(USER_INFO)?.get()
                ?.addOnSuccessListener {
                    val lenderInfo = it.toObject(User::class.java)
                    if(lenderInfo == null) return@addOnSuccessListener

                    val phoneNumber = lenderInfo.phoneNumber
                    val lenderName = lenderInfo.firstName

                    if(isCall) {
                        callLender(phoneNumber)
                    }
                    else {
                        textLender(phoneNumber, lenderName, bookTitle, bookAuthor, borrowerName)
                    }
                }
    }

    private fun callLender(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null))
        startActivity(callIntent)
    }

    private fun textLender(phoneNumber: String, lenderName: String, bookTitle: String, bookAuthor: String, borrowerName: String) {
        val message = "Hi $lenderName, this is $borrowerName from Share-A-Book. " +
                "Where would you like me to pick up the book - $bookTitle by $bookAuthor?"

        val uri = Uri.parse("smsto:$phoneNumber")
        val textIntent = Intent(Intent.ACTION_SENDTO, uri)
        textIntent.putExtra("sms_body", message)
        startActivity(textIntent)
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