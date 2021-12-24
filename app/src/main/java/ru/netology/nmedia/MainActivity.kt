package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.netology.adapter.ClickCallback
import ru.netology.adapter.PostAdapter
import ru.netology.extension.hideKeyboard
import ru.netology.extension.showKeyboard
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.vm.PostViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: PostViewModel by viewModels()
    private var postAdapter: PostAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postAdapter = PostAdapter(object : ClickCallback {
            override fun onLikeClick(position: Int) {
                postAdapter?.apply {
                    viewModel.likeById(this.currentList[position].id)
                }
            }

            override fun onShareClick(position: Int) {
                postAdapter?.apply {
                    viewModel.shareById(this.currentList[position].id)
                }
            }

            override fun onRemoveClick(position: Int) {
                postAdapter?.apply {
                    viewModel.removeById(this.currentList[position].id)
                }
            }

            override fun onEditClick(position: Int) {
                postAdapter?.apply {
                    val existed = this.currentList[position]
                    viewModel.edit(existed)
                }
            }
        })

        binding.recycler.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recycler.adapter = postAdapter
        viewModel.data.observe(this, {
            postAdapter?.apply {
                val isPostAdd = itemCount < it.size
                submitList(it) {
                    if (isPostAdd) {
                        binding.recycler.smoothScrollToPosition(0)
                    }
                }
            }
        })

        viewModel.editPost.observe(this, { post ->
            if (post.id != 0) {
                showEditLayout(post)
            }
        })
        binding.cancel.setOnClickListener {
            viewModel.cancel()
            hideEditLayout()
        }
        binding.save.setOnClickListener {
            val newPostText = binding.content.text.toString()
            if (newPostText.isBlank()) {
                Toast.makeText(
                    binding.root.context,
                    R.string.error_empty_context,
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.changeContent(newPostText)
            viewModel.save()

            hideEditLayout()
        }
    }

    private fun showEditLayout(post: Post) {
        binding.oldContent.text = post.content
        binding.editCancelGroup.visibility = View.VISIBLE
        binding.content.setText(post.content)
        binding.content.setSelection(post.content.length)
        binding.content.showKeyboard()
    }

    private fun hideEditLayout() {
        binding.editCancelGroup.visibility = View.GONE
        binding.content.setText("")
        binding.content.clearFocus()
        binding.content.hideKeyboard()
    }
}
