package edu.uco.ychong.shareabook.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude

class Upload(var name: String, var url: String): Parcelable {

    constructor(): this("", "")

    @get:Exclude
    var id: String = ""

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Upload> {
        override fun createFromParcel(parcel: Parcel): Upload {
            return Upload(parcel)
        }

        override fun newArray(size: Int): Array<Upload?> {
            return arrayOfNulls(size)
        }
    }
}