package edu.uco.ychong.shareabook.book

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.R

class BookListingFragment : Fragment() {
    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        val inflatedView = inflater.inflate(R.layout.fragment_book_listing, container, false)


        return inflatedView
    }
}
