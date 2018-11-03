package edu.uco.ychong.shareabook.user

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.UPDATED_USER_INFO
import edu.uco.ychong.shareabook.USER_INFO
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.model.User
import kotlinx.android.synthetic.main.activity_account_info.*

const val TAG = "ACCOUNT_INFO_ACTIVITY"

class AccountInfoActivity : AppCompatActivity() {
    private var db: FirebaseFirestore ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_info)

        db = FirebaseFirestore.getInstance()

        val userInfo = intent.getParcelableExtra<User>(USER_INFO)

        id_accountInfoFirstNameInput.setText(userInfo.firstName)
        id_accountInfoLastNameInput.setText(userInfo.lastName)
        id_accountInfoPhoneNumberInput.setText(userInfo.phoneNumber)
        id_accountInfoEmailInput.setText(userInfo.email)
        id_accountInfoPasswordInput.setText(userInfo.password)
        id_accountInfoPasswordConfirmationInput.setText(userInfo.passwordConfirm)

        id_accountInfoEmailInput.isEnabled = false

        id_updateAccountInfoButton.setOnClickListener {
            val fName = id_accountInfoFirstNameInput.text.toString().trim()
            val lName = id_accountInfoLastNameInput.text.toString().trim()
            val pNumber = id_accountInfoPhoneNumberInput.text.toString().trim()
            val password = id_accountInfoPasswordInput.text.toString().trim()
            val passwordConfirmation = id_accountInfoPasswordConfirmationInput.text.toString().trim()

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

            val updatedAccountInfo = User(fName, lName, pNumber, userInfo.email, password, passwordConfirmation)

            val intent = Intent()
            intent.putExtra(UPDATED_USER_INFO, updatedAccountInfo)
            setResult(Activity.RESULT_OK, intent)

            finish()
        }

        id_cancelUpdateButton.setOnClickListener {
            onBackPressed()
        }
    }
}
