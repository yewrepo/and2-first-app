package ru.netology.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.netology.adapter.ClickCallback
import ru.netology.adapter.PostAdapter
import ru.netology.extension.navigate
import ru.netology.extension.openYoutube
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.vm.PostViewModel

class FeedFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentFeedBinding
    private lateinit var recyclerManager: LinearLayoutManager

    private lateinit var postAdapter: PostAdapter
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postAdapter = PostAdapter(object : ClickCallback {
            override fun onOpenClick(position: Int) {
                postAdapter.apply {
                    navigate(
                        R.id.action_feedFragment_to_fullscreenPostFragment,
                        currentList[position]
                    )
                }
            }

            override fun onLikeClick(position: Int) {
                postAdapter.apply {
                    val post = this.currentList[position]
                    viewModel.likeById(post.id, post.likedByMe)
                }
            }

            override fun onShareClick(position: Int) {
                postAdapter.apply {
                    //viewModel.shareById(this.currentList[position].id)
                }
            }

            override fun onRemoveClick(position: Int) {
                postAdapter.apply {
                    viewModel.removeById(this.currentList[position].id)
                }
            }

            override fun onEditClick(position: Int) {
                postAdapter.apply {
                    val existed = this.currentList[position]
                    viewModel.edit(existed)
                }
            }

            override fun onYoutubeLinkClick(position: Int) {
                postAdapter.apply {
                    this.currentList[position].openYoutube(requireActivity())
                }
            }

            override fun onPhotoOpenClick(position: Int) {
                postAdapter.apply {
                    navigate(
                        R.id.action_feedFragment_to_fullscreenImageFragment,
                        post = this.currentList[position]
                    )
                }
            }
        })

        binding.newPostsNotify.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }
        binding.swiper.setOnRefreshListener(this)
        recyclerManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recycler.layoutManager = recyclerManager
        binding.recycler.adapter = postAdapter

        viewModel.loadingState.observe(viewLifecycleOwner, { loadingState ->
            binding.recycler.isVisible = !loadingState.isLoading && !loadingState.isError
            binding.swiper.isRefreshing = loadingState.isLoading
            binding.errorGroup.isVisible = loadingState.isError
            binding.newPostsNotify.isVisible = loadingState.newPostNotify
            loadingState.errorDescription?.apply {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.data.observe(viewLifecycleOwner, { feedModel ->
            val oldItemsCount = postAdapter.currentList.size
            postAdapter.submitList(feedModel.posts) {
                if (oldItemsCount < feedModel.posts.size) {
                    recyclerManager.scrollToPosition(0)
                }
            }
            binding.recycler.isVisible = true
            binding.emptyText.isVisible = feedModel.empty
        })

        viewModel.editPost.observe(viewLifecycleOwner, { post ->
            if (post.id != 0L) {
                navigate(R.id.action_feedFragment_to_changePostFragment, post)
            }
        })

        binding.addNewFab.setOnClickListener {
            navigate(R.id.action_feedFragment_to_changePostFragment)
        }

        viewModel.requestUpdates()
    }

    override fun onResume() {
        super.onResume()
        postAdapter.apply {
            if (itemCount == 0) {
                viewModel.loadPosts()
            }
        }
    }

    override fun onRefresh() {
        viewModel.loadPosts()
    }
}