package ru.netology.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.extension.shortFormat
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding

class PostViewHolder(
    private val binding: CardPostBinding,
    private val callback: ClickCallback
) : RecyclerView.ViewHolder(binding.root) {

    init {
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

    fun bind(post: Post?) {
        post?.apply {
            setViews(post)
            setShare(post)
            setLikes(post)
            setYoutubeLink(post)
            with(binding) {
                author.text = post.author
                content.text = post.content
                published.text = post.published
            }
        }
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
