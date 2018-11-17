package edu.uco.ychong.shareabook.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude

class BorrowRequest(var borrowerName: String,
                    var borrowerEmail: String,
                    var borrowerNumber: String,
                    var borrowStatus: String,
                    var borrowDate: String,
                    var returnedDate: String) : Parcelable {

    constructor() : this("",
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
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(borrowerName)
        parcel.writeString(borrowerEmail)
        parcel.writeString(borrowerNumber)
        parcel.writeString(borrowStatus)
        parcel.writeString(borrowDate)
        parcel.writeString(returnedDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BorrowRequest> {
        override fun createFromParcel(parcel: Parcel): BorrowRequest {
            return BorrowRequest(parcel)
        }

        override fun newArray(size: Int): Array<BorrowRequest?> {
            return arrayOfNulls(size)
        }
    }
}