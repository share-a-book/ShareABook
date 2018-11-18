package edu.uco.ychong.shareabook.user

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.uco.ychong.shareabook.MainActivity
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.USER_INFO
import edu.uco.ychong.shareabook.USER_PROFILE
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.helper.FormValidator
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.model.Upload
import edu.uco.ychong.shareabook.model.User
import kotlinx.android.synthetic.main.activity_sign_up.*

const val ACCOUNT_DOC_PATH = "account/accountDoc"
const val PROFILE_STORAGE_PATH = "account/accountDoc"
const val PICK_FROM_GALLERY = 101

class SignUpActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth ?= null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private var fileUriPath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()

        id_createAccountButton.setOnClickListener {
            createUserAccount()
        }

        id_editProfileImage.setOnClickListener {
            openGallery()
        }
    }

    private fun createUserAccount() {
        if (validateUserInputs()) {
            val userInfo = createUserInfo()
            uploadProfileImageToFirebase(userInfo.email)
            mAuth?.createUserWithEmailAndPassword(userInfo.email, userInfo.password)
                ?.addOnCompleteListener {
                    if(it.isSuccessful) {
                        ToastMe.message(this, "Account creation successful!")
                        saveUserInfo(userInfo)
                        uploadProfileImageToFirebase(userInfo.email)
                    } else {
                        ToastMe.message(this, "Account creation failed.\n${it.exception.toString()}")
                    }
                }
        }
    }

    private fun saveUserInfo(userInfo: User) {
        mFireStore?.collection("$ACCOUNT_DOC_PATH/${userInfo.email}")
            ?.document(USER_INFO)
            ?.set(userInfo)
            ?.addOnSuccessListener {
                ToastMe.message(this, "Account info added!")
            }
            ?.addOnFailureListener {
                ToastMe.message(this, "${it.message}")
            }

    }

    private fun validateUserInputs(): Boolean {
        val fName = id_firstNameInput.text.toString().trim()
        val lName = id_lastNameInput.text.toString().trim()
        val pNumber = id_phoneNumberInput.text.toString().trim()
        val email = id_signUpEmailInput.text.toString().trim()
        val password = id_signUpPasswordInput.text.toString().trim()
        val passwordConfirmation = id_signUpPasswordConfirmInput.text.toString().trim()

        if (!FormValidator.validateTextInputFilled(fName)) {
            ToastMe.message(this, "Please enter your first name.")
            return false
        }

        if (!FormValidator.validateTextInputFilled(lName)) {
            ToastMe.message(this, "Please enter your last name.")
            return false
        }
        if (!FormValidator.validatePhone(pNumber)) {
            ToastMe.message(this, "Invalid phone number!")
            return false
        }

        if (!FormValidator.validateTextInputFilled(email)) {
            ToastMe.message(this, "Invalid email input.")
            return false
        }

        if (!FormValidator.validatePassword(password)) {
            ToastMe.message(this, "Invalid password.")
            return false
        }

        if (!FormValidator.validatePassword(passwordConfirmation)) {
            ToastMe.message(this, "Invalid password confirmation!")
            return false
        }

        if (password != passwordConfirmation) {
            ToastMe.message(this, "Password and Password Confirmation don't match!")
            return false
        }
        return true
    }

    private fun createUserInfo(): User {
        val fName = id_firstNameInput.text.toString().trim()
        val lName = id_lastNameInput.text.toString().trim()
        val pNumber = id_phoneNumberInput.text.toString().trim()
        val email = id_signUpEmailInput.text.toString().trim()
        val password = id_signUpPasswordInput.text.toString().trim()
        val passwordConfirmation = id_signUpPasswordConfirmInput.text.toString().trim()
        return User(fName, lName, pNumber, email, password, passwordConfirmation)
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("return-data", true)
        startActivityForResult(Intent.createChooser(intent, "Complete action using"),PICK_FROM_GALLERY)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FROM_GALLERY
                && resultCode == Activity.RESULT_OK
                && data != null
                && data.data != null) {
            showSelectedProfileImage(data.data)
        }
    }

    private fun showSelectedProfileImage(fileUri: Uri) {
        fileUriPath = fileUri
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUriPath)
        id_editProfileImage.setImageBitmap(bitmap)
        id_editImageText.text = "Looking good!"
    }

    private fun uploadProfileImageToFirebase(userId: String) {
        if (fileUriPath != null) {
            val ref = mStorage?.reference?.child("$PROFILE_STORAGE_PATH/$userId/user_profile")
            ref?.putFile(fileUriPath as Uri)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        ref.downloadUrl.addOnSuccessListener {
                            val downloadUrl = it.toString()
                            val uploadImageFile = Upload("user_profile", downloadUrl)
                            mFireStore?.collection("$ACCOUNT_DOC_PATH/$userId")
                                ?.document(USER_PROFILE)
                                ?.set(uploadImageFile)
                                    ?.addOnSuccessListener {
                                        Log.d(TESTTAG, "[SignUpActivity] url: $downloadUrl")
                                        MainActivity.profileUrl = downloadUrl
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                        }
                    }
                }
                ?.addOnFailureListener {
                }
        }
    }
}
