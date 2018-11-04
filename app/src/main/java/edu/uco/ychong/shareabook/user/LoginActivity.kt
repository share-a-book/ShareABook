package edu.uco.ychong.shareabook.user

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.MainActivity
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.model.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth ?= null
    private var db: FirebaseFirestore?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        id_loginButton.setOnClickListener {
            /**
             * Uncomment after testing
             */
            val email = id_loginEmail.text.toString().trim()
            val password = id_loginPassword.text.toString().trim()
//
//            val email = "dattran10@gmail.com"
//            val password = "password"

            if (email.isNullOrEmpty() || email.isNullOrBlank()) {
                Toast.makeText(this, "Invalid email input!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password.isNullOrEmpty() || password.isNullOrBlank()) {
                Toast.makeText(this, "Invalid password input!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener {
                    if(it.isSuccessful) {
                        updatePasswordAfterReset(email, password)

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        ToastMe.message(this, "Logged in successful")
                        finish()
                    }
                    else {
                        ToastMe.message(this, "Logged in fail")
                    }
                }
        }

        id_forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        id_signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updatePasswordAfterReset(email: String, password: String) {
        db?.collection(email)?.document("User Info")?.get()
            ?.addOnSuccessListener {
                val userInfo = it.toObject(User::class.java)

                if (userInfo == null) return@addOnSuccessListener

                val userPassword = userInfo.password

                if (!userPassword.equals(password)) {
                    db?.collection(email)?.document("User Info")
                        ?.update("password", password, "passwordConfirm", password)
                        ?.addOnCompleteListener {
                            ToastMe.message(this, "Account information updated successfully!")
                        }?.addOnFailureListener {
                            ToastMe.message(this, "Account information update failed!")
                        }
                }
            }
            ?.addOnFailureListener {
                ToastMe.message(this, "Failed to get account info!")
            }
    }
}
