package ru.netology.nmedia

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract

val emptyResult = ChangePostData(-1, "")

data class ChangePostData(
    val id: Int,
    val text: String?,
) : Parcelable {

    fun isEmpty() = id == -1

    constructor(post: Post) : this(post.id, post.content)

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(text)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChangePostData> {
        override fun createFromParcel(parcel: Parcel): ChangePostData {
            return ChangePostData(parcel)
        }

        override fun newArray(size: Int): Array<ChangePostData?> {
            return arrayOfNulls(size)
        }
    }

}

class ChangePostResultContract : ActivityResultContract<ChangePostData, ChangePostData>() {

    override fun createIntent(context: Context, input: ChangePostData): Intent =
        Intent(context, ChangePostActivity::class.java).also { intent ->
            intent.putExtra(extraKey, input)
        }

    override fun parseResult(resultCode: Int, intent: Intent?): ChangePostData {
        return intent?.let {
            if (resultCode == RESULT_OK) {
                intent.getParcelableExtra(extraKey)
            } else {
                emptyResult
            }
        } ?: emptyResult
    }

    companion object{
        public const val extraKey = "ChangePostData"
    }
}