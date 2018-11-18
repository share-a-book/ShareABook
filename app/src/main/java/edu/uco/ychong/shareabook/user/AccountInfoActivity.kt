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
import com.squareup.picasso.Picasso
import edu.uco.ychong.shareabook.R
import edu.uco.ychong.shareabook.UPDATED_USER_INFO
import edu.uco.ychong.shareabook.USER_INFO
import edu.uco.ychong.shareabook.USER_PROFILE
import edu.uco.ychong.shareabook.book.TESTTAG
import edu.uco.ychong.shareabook.helper.FormValidator
import edu.uco.ychong.shareabook.helper.ToastMe
import edu.uco.ychong.shareabook.model.Upload
import edu.uco.ychong.shareabook.model.User
import kotlinx.android.synthetic.main.activity_account_info.*

class AccountInfoActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth ?= null
    private var mFireStore: FirebaseFirestore ?= null
    private var mStorage: FirebaseStorage? = null
    private var fileUriPath: Uri? = null
    private lateinit var newProfileUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_info)
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()

        supportActionBar?.hide()

        populateAccountInfoField()

        id_updateAccountInfoButton.setOnClickListener {
            if(!validateAccountInformation())return@setOnClickListener
            val userEmail = mAuth?.currentUser?.email
            uploadProfileImageToFirebase(userEmail)
            finish()
        }

        id_cancelUpdateButton.setOnClickListener {
            onBackPressed()
        }

        id_editProfileImage.setOnClickListener {
            openGallery()
        }
    }

    private fun populateAccountInfoField() {
        val userInfo = intent.getParcelableExtra<User>(USER_INFO)
        val profileUrl = intent.getParcelableExtra<Upload>(USER_PROFILE)


        Log.d(TESTTAG, "populateAccountInfoField: ${profileUrl.url}")
        populateProfileImage()
        id_accountInfoFirstNameInput.setText(userInfo.firstName)
        id_accountInfoLastNameInput.setText(userInfo.lastName)
        id_accountInfoPhoneNumberInput.setText(userInfo.phoneNumber)
        id_accountInfoEmailInput.setText(userInfo.email)
        id_accountInfoPasswordInput.setText(userInfo.password)
        id_accountInfoPasswordConfirmationInput.setText(userInfo.passwordConfirm)
        id_accountInfoEmailInput.isEnabled = false
    }

    private fun populateProfileImage() {
        loadProfileImage()
    }

    private fun loadProfileImage() {
        val userEmail = mAuth?.currentUser?.email
        mFireStore?.collection("$PROFILE_STORAGE_PATH/$userEmail")
            ?.document(USER_PROFILE)
            ?.get()
            ?.addOnSuccessListener {
                val userProfileImage = it.toObject(Upload::class.java)
                if (userProfileImage != null) {
                    Picasso.get().load(userProfileImage.url).into(id_editProfileImage)
                    id_editImageText.text = "Change profile image"
                }
            }
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

        val userEmail = mAuth?.currentUser?.email
        uploadProfileImageToFirebase(userEmail)
    }

    private fun validateAccountInformation(): Boolean {
        var validateIsSuccess = true
        val fName = id_accountInfoFirstNameInput.text.toString().trim()
        val lName = id_accountInfoLastNameInput.text.toString().trim()
        val pNumber = id_accountInfoPhoneNumberInput.text.toString().trim()
        val password = id_accountInfoPasswordInput.text.toString().trim()
        val passwordConfirmation = id_accountInfoPasswordConfirmationInput.text.toString().trim()

        if (!FormValidator.validateTextInputFilled(fName)) {
            ToastMe.message(this, "Please enter your first name.")
            validateIsSuccess = false
        }

        if (!FormValidator.validateTextInputFilled(lName)) {
            ToastMe.message(this, "Please enter your last name.")
            validateIsSuccess = false
        }

        if (!FormValidator.validatePhone(pNumber)) {
            ToastMe.message(this, "Invalid phone number!")
            validateIsSuccess = false
        }

        if (!FormValidator.validatePassword(password)) {
            ToastMe.message(this, "Invalid password.")
            validateIsSuccess = false
        }

        if (!FormValidator.validatePassword(passwordConfirmation)) {
            ToastMe.message(this, "Invalid password confirmation!")
            validateIsSuccess = false
        }

        if (password != passwordConfirmation) {
            ToastMe.message(this, "Password and Password Confirmation don't match!")
            validateIsSuccess = false
        }

        val userInfo = intent.getParcelableExtra<User>(USER_INFO)
        val updatedAccountInfo = User(fName, lName, pNumber, userInfo.email, password, passwordConfirmation)

        val intent = Intent()
        intent.putExtra(UPDATED_USER_INFO, updatedAccountInfo)
        setResult(Activity.RESULT_OK, intent)
        return validateIsSuccess
    }

    private fun uploadProfileImageToFirebase(userId: String?) {
        if (fileUriPath != null && userId != null) {
            val ref = mStorage?.reference?.child("$PROFILE_STORAGE_PATH/$userId/user_profile")
            ref?.putFile(fileUriPath as Uri)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        ref.downloadUrl.addOnSuccessListener {
                            val downloadUrl = it.toString()
                            val uploadImageFile = Upload("user_profile", downloadUrl)
                            newProfileUrl = downloadUrl
                            Log.d(TESTTAG, "[AccountInfoActivity] newProfileUrl: $downloadUrl")
                            mFireStore?.collection("$ACCOUNT_DOC_PATH/$userId")
                                ?.document(USER_PROFILE)
                                ?.set(uploadImageFile)

                        }
                    }
                }
                ?.addOnFailureListener {
                }
        }
    }
}
