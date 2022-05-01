package ru.netology.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.io.File

data class PhotoModel(
    val uri: Uri? = null,
    val file: File? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Uri::class.java.classLoader),
        File(parcel.readString()!!)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(uri, flags)
        parcel.writeString(file!!.absolutePath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhotoModel> {
        override fun createFromParcel(parcel: Parcel): PhotoModel {
            return PhotoModel(parcel)
        }

        override fun newArray(size: Int): Array<PhotoModel?> {
            return arrayOfNulls(size)
        }
    }
}
