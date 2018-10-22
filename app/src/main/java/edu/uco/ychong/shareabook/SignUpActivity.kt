package edu.uco.ychong.shareabook

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
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
                Toast.makeText(this, "Invalid email input!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Invalid password!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordConfirmation.length < 6) {
                Toast.makeText(this, "Invalid password confirmation!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!password.equals(passwordConfirmation)) {
                Toast.makeText(this, "Password and Password Confirmation don't match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        val message = it.exception.toString()
                        Toast.makeText(this, "Account creation failed.\n$message", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        id_cancelCreateAccountButton.setOnClickListener {
            onBackPressed()
        }
    }
}
