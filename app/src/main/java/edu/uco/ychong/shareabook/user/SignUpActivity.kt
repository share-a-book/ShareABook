package edu.uco.ychong.shareabook.user

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.MainActivity
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.USER_INFO
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.model.User
import kotlinx.android.synthetic.main.activity_sign_up.*

const val ACCOUNT_DOC_PATH = "account/accountDoc"
class SignUpActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth ?= null
    private var mFireStore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()

        id_createAccountButton.setOnClickListener {
            val fName = id_firstNameInput.text.toString().trim()
            val lName = id_lastNameInput.text.toString().trim()
            val pNumber = id_phoneNumberInput.text.toString().trim()
            val email = id_signUpEmailInput.text.toString().trim()
            val password = id_signUpPasswordInput.text.toString().trim()
            val passwordConfirmation = id_signUpPasswordConfirmInput.text.toString().trim()

            if (fName.isNullOrEmpty() || fName.isNullOrBlank()) {
                ToastMe.message(this, "Please enter your first name.")
                return@setOnClickListener
            }

            if (lName.isNullOrEmpty() || lName.isNullOrBlank()) {
                ToastMe.message(this, "Please enter your last name.")
                return@setOnClickListener
            }

            if (pNumber.length != 10) {
                ToastMe.message(this, "Invalid phone number!")
                return@setOnClickListener
            }

            if (email.isNullOrEmpty() || email.isNullOrBlank()) {
                ToastMe.message(this, "Invalid email input.")
                return@setOnClickListener
            }

            if (password.length < 6) {
                ToastMe.message(this, "Invalid password.")
                return@setOnClickListener
            }

            if (passwordConfirmation.length < 6) {
                ToastMe.message(this, "Invalid password confirmation!")
                return@setOnClickListener
            }

            if (!password.equals(passwordConfirmation)) {
                ToastMe.message(this, "Password and Password Confirmation don't match!")
                return@setOnClickListener
            }

            mAuth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener {
                    if(it.isSuccessful) {
                        ToastMe.message(this, "Account creation successful!")
                        val userInfo = User(fName, lName, pNumber, email, password, passwordConfirmation)
                        mFireStore?.collection("$ACCOUNT_DOC_PATH/$email")?.document(USER_INFO)?.set(userInfo)
                            ?.addOnSuccessListener {
                                ToastMe.message(this, "Account info added!")
                            }
                            ?.addOnFailureListener {
                                ToastMe.message(this, "${it.message}")
                            }

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        ToastMe.message(this, "Account creation failed.\n${it.exception.toString()}")
                    }
                }
        }
    }
}
