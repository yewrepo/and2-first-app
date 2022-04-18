package ru.netology.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.netology.model.FeedItem
import ru.netology.nmedia.databinding.CardLoadingBinding

class LoadingViewHolder(
    binding: CardLoadingBinding,
) : FeedPostViewHolder(binding.root) {

    override fun bind(feedItem: FeedItem?) {
        //not use
    }

    companion object {

        @JvmStatic
        fun create(parent: ViewGroup): LoadingViewHolder {
            val binding =
                CardLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LoadingViewHolder(binding)
        }
    }
}