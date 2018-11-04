package edu.uco.ychong.shareabook.book

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.model.Book
import edu.uco.ychong.shareabook.model.User
import edu.uco.ychong.shareabook.user.ListingActivity
import edu.uco.ychong.shareabook.user.TAG
import kotlinx.android.synthetic.main.fragment_add_book.*
import kotlinx.android.synthetic.main.fragment_add_book.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

const val BOOKDOC_PATH = "bookDoc/books"

class AddBookFragment: Fragment(), AdapterView.OnItemSelectedListener  {
    private var mAuth: FirebaseAuth?= null
    private var db: FirebaseFirestore? = null
    private var genres = ArrayList<String>()
    private var selectedGenre: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        val inflatedView = inflater.inflate(R.layout.fragment_add_book, container, false)

        val spinner: Spinner = inflatedView.findViewById(R.id.id_categorySpinner)

        spinner.onItemSelectedListener = this

        initializeSpinnerValues()

        val arrayAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, genres)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinner.adapter = arrayAdapter


        inflatedView.id_currentDate.text = getCurrentDateWithFullFormat()

        inflatedView.id_submitButton.setOnClickListener {
            getUserInfoAndAddBook()
        }
        return inflatedView
    }

    private fun getUserInfoAndAddBook() {
        val userEmail = mAuth?.currentUser?.email
        if (userEmail != null) {
            db?.collection(userEmail)?.document("User Info")?.get()
                ?.addOnSuccessListener {
                    val userInfo = it.toObject(User::class.java)
                    if (userInfo == null) return@addOnSuccessListener
                    val lenderName = "${userInfo?.firstName} ${userInfo.lastName}"

                    val newBook = Book(id_bookTitle.text.toString().trim(),
                        id_bookAuthor.text.toString().trim(),
                        id_bookDescription.text.toString().trim(),
                        selectedGenre,
                        lenderName,
                        userEmail,
                        "",
                        BookStatus.AVAILABLE,
                        getCurrentDateWithFullFormat(),
                        "",
                        "")

                    val collectionName = "$userEmail/$BOOKDOC_PATH"
                    val parentActivity = activity as ListingActivity
                    db?.collection(collectionName)?.document()?.set(newBook)
                        ?.addOnSuccessListener {
                            parentActivity.addBookSuccess()
                        }
                        ?.addOnFailureListener {
                            parentActivity.addBookFail()
                        }

                    db?.collection("public/$BOOKDOC_PATH")?.document()?.set(newBook)
                        ?.addOnSuccessListener {
                            parentActivity.addBookSuccess()
                        }
                        ?.addOnFailureListener {
                            parentActivity.addBookFail()
                        }
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        ?.hide(this)?.commit()

                }
        }
    }

    private fun getCurrentDateWithFullFormat(): String {
        return LocalDateTime.now()
            .format(DateTimeFormatter
                .ofLocalizedDate(FormatStyle.FULL))
    }

    private fun initializeSpinnerValues() {
        genres.add("Action and Adventure")
        genres.add("Art")
        genres.add("Science Fiction")
        genres.add("Self help")
        genres.add("Fantasy")
        genres.add("Programming")
        genres.add("Travel")
        genres.add("Other")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        selectedGenre = genres[position]
        Log.d(TAG, "${selectedGenre}")
    }
}