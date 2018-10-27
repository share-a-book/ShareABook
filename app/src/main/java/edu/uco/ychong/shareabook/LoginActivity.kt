package edu.uco.ychong.shareabook

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import edu.uco.ychong.shareabook.helper.ToastMe

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        id_loginButton.setOnClickListener {
            val email = id_loginEmail.text.toString().trim()
            val password = id_loginPassword.text.toString().trim()
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

        id_backButton.setOnClickListener {
            onBackPressed()
        }
    }
}
