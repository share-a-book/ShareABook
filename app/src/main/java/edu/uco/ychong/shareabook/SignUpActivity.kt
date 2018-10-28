package edu.uco.ychong.shareabook

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import edu.uco.ychong.shareabook.helper.ToastMe
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        mAuth = FirebaseAuth.getInstance()

        id_createAccountButton.setOnClickListener {
            val email = id_signUpEmailInput.text.toString().trim()
            val password = id_signUpPasswordInput.text.toString().trim()
            val passwordConfirmation = id_signUpPasswordConfirmInput.text.toString().trim()

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
                        ToastMe.message(this, "Password and Password Confirmation don't match!")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        ToastMe.message(this, "Account creation failed.\n${it.exception.toString()}")
                    }
                }
        }
    }
}
