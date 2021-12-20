package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.netology.adapter.ClickCallback
import ru.netology.adapter.PostAdapter
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
        })

        binding.recycler.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recycler.adapter = postAdapter
        viewModel.data.observe(this, {
            postAdapter?.submitList(it)
        })
    }
}
