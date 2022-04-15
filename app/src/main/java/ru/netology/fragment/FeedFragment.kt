package ru.netology.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.adapter.ClickCallback
import ru.netology.adapter.PostAdapter
import ru.netology.extension.isLoading
import ru.netology.extension.navigate
import ru.netology.extension.openYoutube
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.vm.AuthViewModel
import ru.netology.vm.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentFeedBinding
    private lateinit var recyclerManager: LinearLayoutManager

    private lateinit var postAdapter: PostAdapter

    private val authViewModel: AuthViewModel by activityViewModels()
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
                postAdapter.peek(position)?.apply {
                    navigate(
                        R.id.action_feedFragment_to_fullscreenPostFragment, post = this
                    )
                }
            }

            override fun onLikeClick(position: Int) {
                postAdapter.peek(position)?.apply {
                    viewModel.likeById(id, likedByMe)
                }
            }

            override fun onShareClick(position: Int) {
                postAdapter.peek(position)?.apply {
                    //viewModel.shareById(this.currentList[position].id)
                }
            }

            override fun onRemoveClick(position: Int) {
                postAdapter.peek(position)?.apply {
                    viewModel.removeById(id)
                }
            }

            override fun onEditClick(position: Int) {
                postAdapter.peek(position)?.apply {
                    viewModel.edit(this)
                }
            }

            override fun onYoutubeLinkClick(position: Int) {
                postAdapter.peek(position)?.apply {
                    openYoutube(requireActivity())
                }
            }

            override fun onPhotoOpenClick(position: Int) {
                postAdapter.peek(position)?.apply {
                    navigate(
                        R.id.action_feedFragment_to_fullscreenImageFragment, post = this
                    )
                }
            }
        })

        binding.newPostsNotify.setOnClickListener {
            postAdapter.refresh()
            binding.newPostsNotify.isVisible = false
        }

        binding.retryButton.setOnClickListener {
            postAdapter.retry()
        }
        binding.swiper.setOnRefreshListener(this)
        recyclerManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recycler.layoutManager = recyclerManager
        binding.recycler.adapter = postAdapter

        authViewModel.data.observe(viewLifecycleOwner) {
            postAdapter.refresh()
        }

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                postAdapter.submitData(it)
                binding.errorGroup.isVisible = false
            }
        }

        lifecycleScope.launchWhenCreated {
            postAdapter.loadStateFlow.collectLatest { state ->
                binding.swiper.isRefreshing = state.isLoading()
                binding.recycler.isVisible = state.refresh !is LoadState.Error
                binding.errorGroup.isVisible = state.refresh is LoadState.Error
            }
        }

        binding.swiper.setOnRefreshListener(postAdapter::refresh)

        viewModel.newPostsNotify.observe(viewLifecycleOwner) {
            binding.newPostsNotify.isVisible = it.newPostNotify
        }

        viewModel.editPost.observe(viewLifecycleOwner) { post ->
            if (post.id != 0L) {
                navigate(R.id.action_feedFragment_to_changePostFragment, post)
            }
        }

        binding.addNewFab.setOnClickListener {
            if (authViewModel.authenticated) {
                navigate(R.id.action_feedFragment_to_changePostFragment)
            } else {
                navigate(R.id.action_feedFragment_to_authFragment)
            }
        }

        viewModel.requestUpdates()
    }

    override fun onResume() {
        super.onResume()
        postAdapter.apply {
            if (itemCount == 0) {
                postAdapter.refresh()
            }
        }
    }

    override fun onRefresh() {
        postAdapter.refresh()
    }
}