package edu.uco.ychong.shareabook.user.history.checkout

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.uco.ychong.shareabook.R

class CheckedOutFragment: Fragment() {
    private var mAuth: FirebaseAuth? = null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null

    override fun onCreateView(inflater: LayoutInflater, container:
    ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_checkedout, container, false)
    }
}