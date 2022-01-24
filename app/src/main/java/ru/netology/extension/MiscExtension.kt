package ru.netology.extension

import android.content.Context
import android.icu.text.CompactDecimalFormat
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import ru.netology.nmedia.ChangePostData
import ru.netology.nmedia.Post
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

object PostDataArg : ReadWriteProperty<Bundle, ChangePostData?> {
    override fun getValue(thisRef: Bundle, property: KProperty<*>): ChangePostData? {
        return thisRef.getParcelable(property.name)
    }

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: ChangePostData?) {
        thisRef.putParcelable(property.name, value)
    }
}