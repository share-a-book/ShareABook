package edu.uco.ychong.shareabook.model

import android.os.Parcel
import android.os.Parcelable


class Book(var title: String,
           var author: String,
           var description: String,
           var genre: String,
           var lenderName: String,
           var lenderEmail: String,
           var status: String,
           var datePosted: String,
           var imageUrl: String) : Parcelable {

    constructor(): this("",
        "",
        "",
        "other",
        "",
        "",
        BookStatus.AVAILABLE,
        "",
        "")

//    @get:Exclude
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

    override fun toString(): String {
        return "$title\n$author $id"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(description)
        parcel.writeString(genre)
        parcel.writeString(lenderName)
        parcel.writeString(lenderEmail)
        parcel.writeString(status)
        parcel.writeString(datePosted)
        parcel.writeString(id)
        parcel.writeString(imageUrl)
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