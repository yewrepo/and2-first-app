package ru.netology.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

    private var postAdapter: PostAdapter? = null
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
                postAdapter?.apply {
                    navigate(
                        R.id.action_feedFragment_to_fullscreenPostFragment,
                        currentList[position]
                    )
                }
            }

            override fun onLikeClick(position: Int) {
                postAdapter?.apply {
                    val post = this.currentList[position]
                    viewModel.likeById(post.id, post.likedByMe)
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
                    this.currentList[position].openYoutube(requireActivity())
                }
            }
        })

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }
        binding.swiper.setOnRefreshListener(this)
        binding.recycler.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recycler.adapter = postAdapter
        viewModel.data.observe(viewLifecycleOwner, { feedModel ->
            postAdapter?.submitList(feedModel.posts)
            binding.errorGroup.isVisible = feedModel.error
            binding.emptyText.isVisible = feedModel.empty
            binding.swiper.isRefreshing = feedModel.loading
            feedModel.errorDescription?.apply {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.editPost.observe(viewLifecycleOwner, { post ->
            if (post.id != 0) {
                navigate(R.id.action_feedFragment_to_changePostFragment, post)
            }
        })
        binding.addNewFab.setOnClickListener {
            navigate(R.id.action_feedFragment_to_changePostFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        postAdapter?.apply {
            if (itemCount == 0) {
                viewModel.loadPosts()
            }
        }
    }

    override fun onRefresh() {
        viewModel.loadPosts()
    }

}