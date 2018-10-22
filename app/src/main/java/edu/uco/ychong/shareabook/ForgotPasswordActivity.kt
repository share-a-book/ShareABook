package edu.uco.ychong.shareabook

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        mAuth = FirebaseAuth.getInstance()

        id_resetPasswordButton.setOnClickListener {

            val email = id_forgotPasswordEmail.text.toString().trim()

            if (email.isNullOrBlank() || email.isNullOrEmpty()) {
                Toast.makeText(this, "Invalid email input!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth?.sendPasswordResetEmail(email)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Reset password email sent to $email", Toast.LENGTH_SHORT).show()
                    } else {
                        val message = it.exception.toString()
                        Toast.makeText(this, "Fail to send reset password email.\n$message", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        id_cancelResetPasswordButton.setOnClickListener {
            onBackPressed()
        }
    }
}
