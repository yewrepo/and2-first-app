package ru.netology.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
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
        binding.likes.setOnClickListener {
            callback.onLikeClick(bindingAdapterPosition)
        }
        binding.share.setOnClickListener {
            callback.onShareClick(bindingAdapterPosition)
        }
    }

    fun bind(post: Post?) {
        post?.apply {
            setViews(post)
            setShare(post)
            setLikes(post)
            with(binding) {
                author.text = post.author
                content.text = post.content
                published.text = post.published
            }
        }
    }

    private fun setViews(post: Post) {
        binding.views.text = post.view.shortFormat()
    }

    private fun setShare(post: Post) {
        binding.share.text = post.share.shortFormat()
    }

    private fun setLikes(post: Post) {
        binding.likes.text = post.likes.shortFormat()
        val resId =
            if (post.likedByMe) R.drawable.ic_fill_liked_24dp else R.drawable.ic_fill_like_24dp
        binding.likes.setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0)
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
