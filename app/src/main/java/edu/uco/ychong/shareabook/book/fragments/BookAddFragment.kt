package edu.uco.ychong.shareabook.book.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.USER_INFO
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.helper.DateManager
import edu.uco.ychong.shareabook.helper.Genre
import edu.uco.ychong.shareabook.model.Book
import edu.uco.ychong.shareabook.model.BookStatus
import edu.uco.ychong.shareabook.model.User
import edu.uco.ychong.shareabook.user.ACCOUNT_DOC_PATH
import edu.uco.ychong.shareabook.user.BOOK_IMAGE_STORAGE_PATH
import edu.uco.ychong.shareabook.user.ListingActivity
import edu.uco.ychong.shareabook.user.PICK_FROM_GALLERY
import kotlinx.android.synthetic.main.fragment_book_add.*
import kotlinx.android.synthetic.main.fragment_book_add.view.*
import java.util.*

const val BOOKDOC_PATH = "public/bookDoc/books"
const val REQUESTDOC_PATH = "public/requestDoc/requests"
const val HISTORYDOC_PATH = "public/historyDoc/histories"

class BookAddFragment: Fragment(), AdapterView.OnItemSelectedListener  {
    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private var genres = ArrayList<String>()
    private var selectedGenre: String = ""
    private var fileUriPath: Uri? = null
    private var uploadedUrl: String? = null

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

        addBookFragmentView.id_attachImage.setOnClickListener {
            openGallery()
        }
    }

    private fun getUserInfoAndAddBook() {
        val userEmail = mAuth?.currentUser?.email
        if (userEmail != null) {
            mFireStore?.collection("$ACCOUNT_DOC_PATH/$userEmail")?.document(USER_INFO)?.get()
                ?.addOnSuccessListener {
                    if (!getUserInfo(it)) return@addOnSuccessListener
                }
        }
    }

    private fun uploadBookImage() {
        val userEmail = mAuth?.currentUser?.email
        if (isUploadFileExist() && userEmail != null) {
            val ref = mStorage
                ?.reference
                ?.child("$BOOK_IMAGE_STORAGE_PATH/$userEmail/${UUID.randomUUID()}")
            ref?.putFile(fileUriPath as Uri)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        ref.downloadUrl.addOnSuccessListener {
                            val downloadUrl = it.toString()
                            uploadedUrl = downloadUrl
                            Log.d(TESTTAG,"[BookAddFragment]: uploadedUrl $uploadedUrl")
                        }
                    }
                }
        }
    }

    private fun getUserInfo(it: DocumentSnapshot): Boolean {
        val userEmail = mAuth?.currentUser?.email
        val userInfo = it.toObject(User::class.java) ?: return false
        val lenderName = "${userInfo?.firstName} ${userInfo.lastName}"
        if (userEmail != null) {
            var imageUrl = uploadedUrl

            val newBook = Book(
                id_bookTitle.text.toString().trim(),
                id_bookAuthor.text.toString().trim(),
                id_bookDescription.text.toString().trim(),
                selectedGenre,
                lenderName,
                userEmail,
                BookStatus.AVAILABLE,
                DateManager.getCurrentDateWithFullFormat(),
                "$imageUrl"
            )
            addNewBookToFireStore(newBook)
        }
        return true
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

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("return-data", true)
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FROM_GALLERY
            && resultCode == Activity.RESULT_OK
            && data != null
            && data.data != null) {
            showSelectedBookImage(data.data)
            uploadBookImage()
        }
    }

    private fun showSelectedBookImage(fileUri: Uri) {
        fileUriPath = fileUri
        val contentResolver = activity?.contentResolver
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
        id_attachImage.setImageBitmap(bitmap)
        Log.d(TESTTAG, fileUriPath.toString())
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

    private fun isUploadFileExist(): Boolean {
        if (fileUriPath != null)
            return true
        return false
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