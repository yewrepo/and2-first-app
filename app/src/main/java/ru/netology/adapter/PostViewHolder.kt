package ru.netology.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import ru.netology.extension.getContext
import ru.netology.extension.getRemoteAvatarRoute
import ru.netology.extension.getRemoteMediaRoute
import ru.netology.extension.shortFormat
import ru.netology.model.FeedItem
import ru.netology.model.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding

class PostViewHolder(
    private val binding: CardPostBinding,
    private val callback: ClickCallback
) : FeedPostViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            callback.onOpenClick(bindingAdapterPosition)
        }
        binding.postImage.setOnClickListener {
            callback.onPhotoOpenClick(bindingAdapterPosition)
        }
        binding.youtubeButton.setOnClickListener {
            callback.onYoutubeLinkClick(bindingAdapterPosition)
        }
        binding.likes.setOnClickListener {
            callback.onLikeClick(bindingAdapterPosition)
        }
        binding.share.setOnClickListener {
            callback.onShareClick(bindingAdapterPosition)
        }
        binding.menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.post_options)
                setOnMenuItemClickListener { item ->
                    return@setOnMenuItemClickListener when (item.itemId) {
                        R.id.remove -> {
                            callback.onRemoveClick(bindingAdapterPosition)
                            true
                        }
                        R.id.edit -> {
                            callback.onEditClick(bindingAdapterPosition)
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }
    }

    override fun bind(feedItem: FeedItem?) {
        (feedItem as Post).apply {
            loadAvatar(feedItem)
            loadImage(feedItem)
            setViews(feedItem)
            setShare(feedItem)
            setLikes(feedItem)
            setYoutubeLink(feedItem)
            with(binding) {
                author.text = feedItem.author
                content.text = feedItem.content
                published.text = feedItem.published.toString()
                menu.isVisible = feedItem.ownedByMe
            }
        }
    }

    private fun loadImage(post: Post) {
        binding.postImage.isVisible = post.attachment != null
        post.attachment?.apply {
            Glide.with(getContext())
                .load(getRemoteMediaRoute())
                .timeout(10_000)
                .fitCenter()
                .into(binding.postImage)
        }
    }

    private fun loadAvatar(post: Post) {
        Glide.with(getContext())
            .load(post.getRemoteAvatarRoute())
            .timeout(10_000)
            .placeholder(R.drawable.ic_broken_image_24dp)
            .centerCrop()
            .into(binding.avatar)
    }

    private fun setYoutubeLink(post: Post) {
        binding.youtubeButton.visibility =
            if (post.youtubeLink.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    private fun setViews(post: Post) {
        binding.views.text = post.view.shortFormat()
    }

    private fun setShare(post: Post) {
        binding.share.text = post.share.shortFormat()
    }

    private fun setLikes(post: Post) {
        binding.likes.text = post.likes.shortFormat()
        binding.likes.isChecked = post.likedByMe
    }

    companion object {

        @JvmStatic
        fun create(parent: ViewGroup, callback: ClickCallback): PostViewHolder {
            val binding =
                CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PostViewHolder(binding, callback)
        }
    }
}
