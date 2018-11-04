package edu.uco.ychong.shareabook.helper

import com.google.firebase.auth.FirebaseUser

class UserAccess {
    companion object {
        fun isLoggedIn(currentUser: FirebaseUser?): Boolean = currentUser != null
        fun isLoggedIn(currentUser: String): Boolean = currentUser != null
    }
}