package ru.netology.extension

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.icu.text.CompactDecimalFormat
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.netology.fragment.ChangePostFragment.Companion.postData
import ru.netology.nmedia.Attachment
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.notification.Notifications
import ru.netology.repository.NetworkPostRepositoryImpl
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun Int.shortFormat(): String {
    if (this > 0) {
        val formatter = CompactDecimalFormat.getInstance(
            Locale.ENGLISH,
            CompactDecimalFormat.CompactStyle.SHORT
        )
        return formatter.format(this)
    }
    return "0"
}

fun Post.likeOrNot(): Post {
    return this.copy(
        likes = if (this.likedByMe) this.likes.dec() else this.likes.inc(),
        likedByMe = this.likedByMe.not()
    )
}

fun Post.shareMe(): Post {
    return this.copy(
        share = this.share.inc()
    )
}

fun View?.hideKeyboard() = this?.apply {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View?.showKeyboard() = this?.apply {
    if (this is EditText) {
        requestFocus()
        postDelayed({
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, 0)
        }, 100)
    }
}

fun Post?.openYoutube(activity: Activity) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(this?.youtubeLink))
        activity.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(
            activity,
            R.string.error_youtube_link,
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun Fragment.navigate(id: Int, post: Post? = null) {
    findNavController().navigate(id, Bundle().apply {
        postData = post ?: Post.emptyPost()
    })
}

object PostDataArg : ReadWriteProperty<Bundle, Post?> {
    override fun getValue(thisRef: Bundle, property: KProperty<*>): Post? {
        return thisRef.getParcelable(property.name)
    }

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: Post?) {
        thisRef.putParcelable(property.name, value)
    }
}

fun Application.initChannels() {
    Notifications(applicationContext).initChannels()
}

fun RecyclerView.ViewHolder.getContext() = itemView.context

fun Attachment.getRemoteMediaRoute(): String {
    return "${NetworkPostRepositoryImpl.BASE_URL}/media/${url}"
}

fun Post.getRemoteAvatarRoute(): String {
    return "${NetworkPostRepositoryImpl.BASE_URL}/avatars/${authorAvatar}"
}