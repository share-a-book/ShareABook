package edu.uco.ychong.shareabook.book

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.EXTRA_SELECTED_BOOK
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.book.fragments.BOOKDOC_PATH
import edu.uco.ychong.shareabook.helper.Genre
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.model.Book
import edu.uco.ychong.shareabook.user.ListingActivity
import kotlinx.android.synthetic.main.activity_book_edit.*

class BookEditActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener   {

    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    private var genres = ArrayList<String>()
    private var selectedGenre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_edit)
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        populateInputFields()
        setupSpinner()

        id_editSubmitButton.setOnClickListener {
            editBookInfo()
        }
    }

    private fun editBookInfo() {
        val bookInfo = intent.getParcelableExtra<Book>(EXTRA_SELECTED_BOOK)
        val bookId = bookInfo.id

        val title = id_editBookTitle.text.toString().trim()
        val author = id_editBookAuthor.text.toString().trim()
        val description = id_editBookDescription.text.toString().trim()
        val category = selectedGenre

        mFireStore?.collection(BOOKDOC_PATH)?.document(bookId)?.update("title", title,
                "author", author, "description", description, "genre", category)
                ?.addOnSuccessListener {

                    ToastMe.message(this, "Book information updated successfully!")
                    startActivity(Intent(this, ListingActivity::class.java))
                    finish()

                }?.addOnFailureListener {
                    ToastMe.message(this, "Book information update failed.")
                }
    }

    private fun populateInputFields() {
        val selectedBookFromExtra = intent.getParcelableExtra<Book>(EXTRA_SELECTED_BOOK)
        id_editBookTitle.setText(selectedBookFromExtra.title)
        id_editBookAuthor.setText(selectedBookFromExtra.author)
        id_editBookDescription.setText(selectedBookFromExtra.description)
        id_editBookDatePosted.text = "Date posted: ${selectedBookFromExtra.datePosted}"
        id_editBookStatus.text = "Status: ${selectedBookFromExtra.status}"
        selectedGenre = selectedBookFromExtra.genre
    }

    private fun setupSpinner() {
        initializeSpinnerItems()
        val spinner = findViewById<Spinner>(R.id.id_editCategorySpinner)
        spinner.onItemSelectedListener = this
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genres)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinner.apply {
            adapter = arrayAdapter
        }

        if (!selectedGenre.isEmpty()) {
            val position = arrayAdapter.getPosition(selectedGenre)
            spinner.setSelection(position)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        selectedGenre = genres[position]
    }

    private fun initializeSpinnerItems() {
        genres = Genre.list
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_book_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.id_deleteBook -> {
                showDeleteAlertDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteAlertDialog() {
        val builder = AlertDialog.Builder(this@BookEditActivity)
        builder.setTitle("Delete Book Listing")
        builder.setMessage("Are you sure you want to delete this listing?")
        builder.setPositiveButton("YES") { dialog, which ->
            deleteBook()
        }.setNegativeButton("NO") { dialog, which ->
            ToastMe.message(this, "You do not want to delete this listing.")
        }.setNeutralButton("CANCEL") { dialog, which ->
            ToastMe.message(this, "Canceled book delete.")
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GRAY)
    }

    private fun deleteBook() {
        val bookInfo = intent.getParcelableExtra<Book>(EXTRA_SELECTED_BOOK)
        val bookId = bookInfo.id

        mFireStore?.collection(BOOKDOC_PATH)?.document(bookId)?.delete()
            ?.addOnSuccessListener {
                ToastMe.message(this, "Book deleted successfully.")
                startActivity(Intent(this, ListingActivity::class.java))
                finish()
            }
            ?.addOnFailureListener {
                ToastMe.message(this, "Book delete failed.")
            }
    }
}
