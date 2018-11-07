package edu.uco.ychong.shareabook.book

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
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
import edu.uco.ychong.shareabook.helper.Genre
import edu.uco.ychong.shareabook.model.Book
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
        populateInputFields()
        setupSpinner()

        id_editSubmitButton.setOnClickListener {
            /**
             * YanFay
             * Implement edit book information here.
             *
             */



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
            Log.d(TESTTAG, position.toString())
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
                /**
                 * YanFay
                 * Implement delete book Here
                 *
                 */




                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
