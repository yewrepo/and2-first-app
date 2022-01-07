package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.netology.adapter.ClickCallback
import ru.netology.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.vm.PostViewModel
import android.content.Intent
import android.net.Uri
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: PostViewModel by viewModels()
    private var postAdapter: PostAdapter? = null

    private val changePostLauncher = registerForActivityResult(
        ChangePostResultContract()
    ) { result ->
        if (result.isEmpty().not()) {
            viewModel.changeContent(result.text!!, result.youtubeLink)
            viewModel.save()
        } else {
            viewModel.cancel()
        }
    }

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

            override fun onYoutubeLinkClick(position: Int) {
                postAdapter?.apply {
                    openYoutube(this.currentList[position])
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
                changePostLauncher.launch(ChangePostData(post))
            }
        })
    }

    private fun openYoutube(post: Post?) {
        post?.apply {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(this.youtubeLink))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    binding.root.context,
                    R.string.error_youtube_link,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_add_post) {
            changePostLauncher.launch(ChangePostData(0, ""))
            true
        } else
            false
    }

}
