package ru.netology.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.netology.model.FeedAd
import ru.netology.model.FeedItem
import ru.netology.model.Post

private val diffCallback = object : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}

class PostAdapter(
    private val callback: ClickCallback
) : PagingDataAdapter<FeedItem, FeedPostViewHolder>(diffCallback) {

    private val typePost = 1
    private val typeAd = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedPostViewHolder {
        return when (viewType) {
            typeAd -> AdViewHolder.create(parent, callback)
            else -> PostViewHolder.create(parent, callback)
        }
    }

    override fun onBindViewHolder(holder: FeedPostViewHolder, position: Int) {
        holder.bind(getItem(holder.bindingAdapterPosition))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FeedAd -> typeAd
            is Post -> typePost
            null -> typePost
        }
    }
}