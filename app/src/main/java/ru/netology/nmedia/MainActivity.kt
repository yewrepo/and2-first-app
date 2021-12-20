package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.extension.shortFormat
import ru.netology.extension.likeOrNot
import ru.netology.extension.shareMe
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.vm.PostViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCallbacks()

        viewModel.data.observe(this, {
            showPost(it)
        })
    }

    private fun setCallbacks() {
        binding.likes.setOnClickListener {
            viewModel.like()
        }
        binding.share.setOnClickListener {
            viewModel.share()
        }
    }

    private fun showPost(post: Post) {
        setViews(post)
        setShare(post)
        setLikes(post)
        with(binding) {
            author.text = post.author
            content.text = post.content
            published.text = post.published
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
}
