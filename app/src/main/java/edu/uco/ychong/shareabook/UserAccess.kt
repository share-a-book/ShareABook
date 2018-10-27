package edu.uco.ychong.shareabook

import com.google.firebase.auth.FirebaseUser

class UserAccess {
    companion object {
        fun isLoggedIn(currentUser: FirebaseUser?): Boolean = currentUser != null
    }
}