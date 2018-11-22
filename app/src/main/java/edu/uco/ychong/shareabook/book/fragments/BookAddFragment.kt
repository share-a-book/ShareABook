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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import edu.uco.ychong.shareabook.MainActivity
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.helper.DateManager
import edu.uco.ychong.shareabook.helper.Genre
import edu.uco.ychong.shareabook.model.Book
import edu.uco.ychong.shareabook.model.BookStatus
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
    private var userEmail: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()
        userEmail = mAuth?.currentUser?.email
        val inflatedView = inflater.inflate(R.layout.fragment_book_add, container, false)
        setupSpinner(inflatedView, container)
        setupBookAddFragmentView(inflatedView)
        return inflatedView
    }

    private fun setupBookAddFragmentView(addBookFragmentView: View) {
        addBookFragmentView.id_currentDate.text = DateManager.getCurrentDateWithFullFormat()

        addBookFragmentView.id_submitButton.setOnClickListener {
            addNewBook()
        }

        addBookFragmentView.id_attachImage.setOnClickListener {
            openGallery()
        }
    }

    private fun addNewBook() {
        if (!isUploadFileExist())
            uploadNewBookWithoutImage()
        else
            uploadNewBookWithImage()

    }

    private fun uploadNewBookWithoutImage() {
        val parentActivity = activity as ListingActivity
        if (userEmail != null) {
            val newBook = Book(
                id_bookTitle.text.toString().trim(),
                id_bookAuthor.text.toString().trim(),
                id_bookDescription.text.toString().trim(),
                selectedGenre,
                MainActivity.userFullName,
                userEmail as String,
                BookStatus.AVAILABLE,
                DateManager.getCurrentDateWithFullFormat(),
                ""
            )
            uploadNewBook(newBook)
        }
    }

    private fun uploadNewBookWithImage() {
        if (isUploadFileExist() && userEmail != null) {
            val ref = mStorage
                ?.reference
                ?.child("$BOOK_IMAGE_STORAGE_PATH/$userEmail/${UUID.randomUUID()}")

            ref?.putFile(fileUriPath as Uri)
                ?.addOnCompleteListener {
                    uploadNewBookImageToStorage(it, ref)
                }
        }
    }

    private fun uploadNewBook(book: Book) {
        val parentActivity = activity as ListingActivity
        mFireStore?.collection("$BOOKDOC_PATH")?.document()?.set(book)
            ?.addOnSuccessListener {
                parentActivity.addBookSuccess()
            }
            ?.addOnFailureListener {
                parentActivity.addBookFail()
            }
    }

    private fun uploadNewBookImageToStorage(it: Task<UploadTask.TaskSnapshot>, ref: StorageReference) {
        if (it.isSuccessful) {
            ref.downloadUrl.addOnSuccessListener {
                val uploadUrl = it.toString()
                Log.d(TESTTAG,"[BookAddFragment]: uploadedUrl $uploadedUrl")
                val newBook = Book(
                    id_bookTitle.text.toString().trim(),
                    id_bookAuthor.text.toString().trim(),
                    id_bookDescription.text.toString().trim(),
                    selectedGenre,
                    MainActivity.userFullName,
                    userEmail as String,
                    BookStatus.AVAILABLE,
                    DateManager.getCurrentDateWithFullFormat(),
                    "$uploadUrl")
                uploadNewBook(newBook)
            }
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

    private fun removePreviousUploadedImage() {
        if (uploadedUrl != null) {
            val ref = mStorage?.reference
                ?.child(uploadedUrl as String)
                ?.delete()
                ?.addOnSuccessListener {
                    Log.d(TESTTAG, "replace image on database successfully.")
                }?.addOnFailureListener {
                    Log.d(TESTTAG, "$it")
                }
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