package edu.uco.ychong.shareabook.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude


class Book(var title: String,
           var author: String,
           var description: String,
           var genre: String,
           var lender: String,
           var lenderEmail: String,
           var borrower: String,
           var status: String,
           var datePosted: String,
           var checkedoutDate: String,
           var checkedoutDuration: String) : Parcelable {

    constructor(): this("",
        "",
        "",
        "other",
        "",
        "",
        "",
        "available",
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
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
        id = parcel.readString()
    }

    override fun toString(): String {
        return "$title\n$author"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(description)
        parcel.writeString(genre)
        parcel.writeString(lender)
        parcel.writeString(lenderEmail)
        parcel.writeString(borrower)
        parcel.writeString(status)
        parcel.writeString(datePosted)
        parcel.writeString(checkedoutDate)
        parcel.writeString(checkedoutDuration)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}