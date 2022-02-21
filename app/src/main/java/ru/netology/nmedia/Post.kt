package ru.netology.nmedia

import android.os.Parcel
import android.os.Parcelable

data class Post(
    val id: Int,
    val author: String?,
    val content: String?,
    val published: Long,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val share: Int = 0,
    val view: Int = 0,
    val youtubeLink: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(author)
        parcel.writeString(content)
        parcel.writeLong(published)
        parcel.writeByte(if (likedByMe) 1 else 0)
        parcel.writeInt(likes)
        parcel.writeInt(share)
        parcel.writeInt(view)
        parcel.writeString(youtubeLink)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }

        @JvmStatic
        fun emptyPost() = Post(
            id = -1,
            author = "",
            content = "",
            published = 0
        )
    }
}
