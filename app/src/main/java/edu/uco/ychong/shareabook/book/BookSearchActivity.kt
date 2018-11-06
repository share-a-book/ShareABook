package edu.uco.ychong.shareabook.book

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.EXTRA_SELECTED_BOOK
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.model.Book
import kotlinx.android.synthetic.main.activity_book_search.*

class BookSearchActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mFireStore: FirebaseFirestore? = null
    private var availableBooks = ArrayList<Book>()
    private var bookAdapter: BookAdapter? = null


    companion object {
        var originalDataSet = ArrayList<Book>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        val currentUser = mAuth?.currentUser
        var viewManager = LinearLayoutManager(this)


        loadAllAvailableBooks()

        bookAdapter = BookAdapter(availableBooks, object: CustomItemClickListener {
            override fun onItemClick(v: View, position: Int) {
                val selectedBook = availableBooks[position]
                goToBookInfoActivity(selectedBook)
            }
        })

        recyclerSearchViewBooks.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = bookAdapter
        }
    }

    private fun loadAllAvailableBooks() {
        val publicBookPath = "$BOOKDOC_PATH"
        mFireStore?.collection(publicBookPath)?.get()?.addOnSuccessListener {
            availableBooks.clear()
            for (bookSnapShot in it) {
                Log.d(edu.uco.ychong.shareabook.user.TAG, bookSnapShot.toString())
                val book =  bookSnapShot.toObject(Book::class.java)
                book.id = bookSnapShot.id
                availableBooks.add(book)
                originalDataSet.add(book)
            }
            val bookAdapter = recyclerSearchViewBooks.adapter
            bookAdapter?.notifyDataSetChanged()
        }?.addOnFailureListener {
            Log.d(edu.uco.ychong.shareabook.user.TAG, it.toString())
        }
    }

    private fun goToBookInfoActivity(selectedBook: Book) {
        val infoIntent = Intent(this, BookInfoActivity::class.java)
        infoIntent.putExtra(EXTRA_SELECTED_BOOK, selectedBook)
        startActivity(infoIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu.findItem(R.id.search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.isSubmitButtonEnabled = true

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                if (text != null)
                bookAdapter?.filter(text)
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                if (text != null)
                    bookAdapter?.filter(text)
                return true
            }

        })
        return true
    }
}
