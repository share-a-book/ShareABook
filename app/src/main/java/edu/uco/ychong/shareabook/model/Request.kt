package edu.uco.ychong.shareabook.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude

class Request(var bookId: String,
              var bookTitle: String,
              var bookAuthor: String,
              var bookImageUrl: String,
              var lenderEmail: String,
              var borrowerEmail: String,
              var borrowerName: String,
              var requestStatus: String,
              var requestDate: String
) : Parcelable {

    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "")

    @get:Exclude
    var id: String = ""

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookId)
        parcel.writeString(bookTitle)
        parcel.writeString(bookAuthor)
        parcel.writeString(bookImageUrl)
        parcel.writeString(lenderEmail)
        parcel.writeString(borrowerEmail)
        parcel.writeString(borrowerName)
        parcel.writeString(requestStatus)
        parcel.writeString(requestDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Request> {
        override fun createFromParcel(parcel: Parcel): Request {
            return Request(parcel)
        }

        override fun newArray(size: Int): Array<Request?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String = "$id $bookTitle $borrowerEmail, $lenderEmail, $requestStatus"
}