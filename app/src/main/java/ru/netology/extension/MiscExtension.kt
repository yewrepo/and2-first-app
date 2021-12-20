package ru.netology.extension

import android.icu.text.CompactDecimalFormat
import ru.netology.nmedia.Post
import java.util.*

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
