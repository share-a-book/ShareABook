package edu.uco.ychong.shareabook.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude

class History(var bookTitle: String,
              var bookAuthor: String,
              var bookImageUrl: String,
              var lenderEmail: String,
              var lenderName: String,
              var borrowerEmail: String,
              var borrowerName: String,
              var historyStatus: String,
              var lastUpdatedDate: String) : Parcelable {

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
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookTitle)
        parcel.writeString(bookAuthor)
        parcel.writeString(bookImageUrl)
        parcel.writeString(lenderEmail)
        parcel.writeString(lenderName)
        parcel.writeString(borrowerEmail)
        parcel.writeString(borrowerName)
        parcel.writeString(historyStatus)
        parcel.writeString(lastUpdatedDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<History> {
        override fun createFromParcel(parcel: Parcel): History {
            return History(parcel)
        }

        override fun newArray(size: Int): Array<History?> {
            return arrayOfNulls(size)
        }
    }

}