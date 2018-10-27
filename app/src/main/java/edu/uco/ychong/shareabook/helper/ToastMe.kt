package edu.uco.ychong.shareabook.helper

import android.content.Context
import android.widget.Toast

class ToastMe {
    companion object {
        fun message(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}