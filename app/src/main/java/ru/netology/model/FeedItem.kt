package ru.netology.model

sealed class FeedItem {
    abstract val id: Long
}

data class FeedAd(
    override val id: Long,
    val url: String,
    val image: String,
) : FeedItem()

data class FeedLoading(
    override val id: Long
) : FeedItem()