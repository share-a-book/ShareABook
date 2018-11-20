package edu.uco.ychong.shareabook.book.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.USER_INFO
import edu.uco.ychong.shareabook.helper.DateManager
import edu.uco.ychong.shareabook.helper.Genre
import edu.uco.ychong.shareabook.model.Book
import edu.uco.ychong.shareabook.model.BookStatus
import edu.uco.ychong.shareabook.model.User
import edu.uco.ychong.shareabook.user.ACCOUNT_DOC_PATH
import edu.uco.ychong.shareabook.user.ListingActivity
import kotlinx.android.synthetic.main.fragment_book_add.*
import kotlinx.android.synthetic.main.fragment_book_add.view.*

const val BOOKDOC_PATH = "public/bookDoc/books"
const val REQUESTDOC_PATH = "public/requestDoc/requests"

class BookAddFragment: Fragment(), AdapterView.OnItemSelectedListener  {
    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private var genres = ArrayList<String>()
    private var selectedGenre: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()
        val inflatedView = inflater.inflate(R.layout.fragment_book_add, container, false)
        setupSpinner(inflatedView, container)
        setupBookAddFragmentView(inflatedView)
        return inflatedView
    }

    private fun setupBookAddFragmentView(addBookFragmentView: View) {
        addBookFragmentView.id_currentDate.text = DateManager.getCurrentDateWithFullFormat()
        addBookFragmentView.id_submitButton.setOnClickListener {
            getUserInfoAndAddBook()
        }
    }

    private fun getUserInfoAndAddBook() {
        val userEmail = mAuth?.currentUser?.email
        if (userEmail != null) {
            mFireStore?.collection("$ACCOUNT_DOC_PATH/$userEmail")?.document(USER_INFO)?.get()
                ?.addOnSuccessListener {
                    val userInfo = it.toObject(User::class.java) ?: return@addOnSuccessListener
                    val lenderName = "${userInfo?.firstName} ${userInfo.lastName}"

                    val newBook = Book(
                        id_bookTitle.text.toString().trim(),
                        id_bookAuthor.text.toString().trim(),
                        id_bookDescription.text.toString().trim(),
                        selectedGenre,
                        lenderName,
                        userEmail,
                        BookStatus.AVAILABLE,
                        DateManager.getCurrentDateWithFullFormat(),
                        "")

                    addNewBookToFireStore(newBook)
                }
        }
    }


    private fun addNewBookToFireStore(newBook: Book) {
        val parentActivity = activity as ListingActivity
        mFireStore?.collection("$BOOKDOC_PATH")?.document()?.set(newBook)
            ?.addOnSuccessListener {
                parentActivity.addBookSuccess()
            }
            ?.addOnFailureListener {
                parentActivity.addBookFail()
            }
    }

    private fun setupSpinner(addBookFragmentView: View, container: ViewGroup?) {
        initializeSpinnerItems()
        val spinner = addBookFragmentView.findViewById<Spinner>(R.id.id_editCategorySpinner)
        spinner.onItemSelectedListener = this
        val arrayAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, genres)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinner.apply {
            adapter = arrayAdapter
        }
    }

    private fun initializeSpinnerItems() {
        genres = Genre.list
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        selectedGenre = genres[position]
    }
}