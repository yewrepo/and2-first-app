package ru.netology.model

import android.os.Parcel
import android.os.Parcelable

data class Post(
    override val id: Long,
    val author: String?,
    val authorId: Long,
    val authorAvatar: String? = null,
    val content: String?,
    val published: Long,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val share: Int = 0,
    val view: Int = 0,
    val youtubeLink: String? = null,
    val isNew: Boolean,
    val ownedByMe: Boolean,
    val photoModel: PhotoModel? = null,
    val attachment: Attachment? = null,
) : FeedItem(), Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(PhotoModel::class.java.classLoader),
        parcel.readParcelable(Attachment::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(author)
        parcel.writeLong(authorId)
        parcel.writeString(authorAvatar)
        parcel.writeString(content)
        parcel.writeLong(published)
        parcel.writeByte(if (likedByMe) 1 else 0)
        parcel.writeInt(likes)
        parcel.writeInt(share)
        parcel.writeInt(view)
        parcel.writeString(youtubeLink)
        parcel.writeByte(if (isNew) 1 else 0)
        parcel.writeByte(if (ownedByMe) 1 else 0)
        parcel.writeParcelable(photoModel, flags)
        parcel.writeParcelable(attachment, flags)
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
    }
}