package ru.netology.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.model.FeedItem

abstract class FeedPostViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
    abstract fun bind(feedItem: FeedItem?)
}