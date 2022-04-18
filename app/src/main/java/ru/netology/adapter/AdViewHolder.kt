package ru.netology.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import ru.netology.Variables
import ru.netology.extension.getContext
import ru.netology.model.FeedAd
import ru.netology.model.FeedItem
import ru.netology.nmedia.databinding.CardAdBinding

class AdViewHolder(
    private val binding: CardAdBinding,
    private val callback: ClickCallback
) : FeedPostViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            callback.onAdOpenClick(bindingAdapterPosition)
        }
    }

    override fun bind(feedItem: FeedItem?) {
        (feedItem as FeedAd).apply {
            val url = "${Variables.BASE_URL}media/${feedItem.image}"
            Glide.with(getContext())
                .load(url)
                .timeout(10_000)
                .fitCenter()
                .into(binding.image)
        }
    }

    companion object {

        @JvmStatic
        fun create(parent: ViewGroup, callback: ClickCallback): AdViewHolder {
            val binding = CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return AdViewHolder(binding, callback)
        }
    }
}