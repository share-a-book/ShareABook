package edu.uco.ychong.shareabook.helper

class FormValidator {

    companion object {
        fun validateTextInputFilled(firstName: String): Boolean {
            if (firstName.isNullOrEmpty() || firstName.isNullOrBlank())
                return false
            return true
        }

        fun validatePhone(phone: String): Boolean {
            if (phone.length != 10)
                return false
            return true
        }

        fun validatePassword(password: String): Boolean {
            if (password.length < 6)
                return false
            return true
        }

    }
}