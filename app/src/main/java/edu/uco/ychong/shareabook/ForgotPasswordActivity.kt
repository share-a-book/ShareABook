package edu.uco.ychong.shareabook

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import edu.uco.ychong.shareabook.helper.ToastMe
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
                ToastMe.message(this, "Invalid email input!")
                return@setOnClickListener
            }

            mAuth?.sendPasswordResetEmail(email)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        ToastMe.message(this, "Reset password email sent to $email")
                    } else {
                        ToastMe.message(this, "Fail to send reset password email.\n${it.exception.toString()}")
                    }
                }
        }

        id_cancelResetPasswordButton.setOnClickListener {
            onBackPressed()
        }
    }
}
