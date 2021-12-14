package ru.netology.extension

import android.icu.text.CompactDecimalFormat
import ru.netology.nmedia.Post
import java.util.*

fun Int.shortFormat(): String = this.let { value ->
    if (value > 0) {
        val formatter = CompactDecimalFormat.getInstance(
            Locale.ENGLISH,
            CompactDecimalFormat.CompactStyle.SHORT
        )
        return@let formatter.format(value)
    }
    return@let "0"
}

fun Post.likeOrNot(): Post = this.let {
    return it.copy(
        likes = if (it.likedByMe) it.likes.dec() else it.likes.inc(),
        likedByMe = it.likedByMe.not()
    )
}

fun Post.shareMe(): Post = this.let {
    return it.copy(
        share = it.share.inc()
    )
}
