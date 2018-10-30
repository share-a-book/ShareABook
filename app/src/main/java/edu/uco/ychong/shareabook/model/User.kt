package edu.uco.ychong.shareabook.model

import android.os.Parcel
import android.os.Parcelable

class User(var firstName: String, var lastName: String, var phoneNumber: String,
           var email: String, var password: String, var passwordConfirm: String) : Parcelable
{
    constructor() : this("", "", "", "", "", "")

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(phoneNumber)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(passwordConfirm)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}