package ru.netology.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.adapter.ClickCallback
import ru.netology.adapter.PostViewHolder
import ru.netology.extension.PostDataArg
import ru.netology.extension.navigate
import ru.netology.extension.openYoutube
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFullscreenPostBinding
import ru.netology.vm.PostViewModel

class FullscreenPostFragment : Fragment() {

    private lateinit var postHolder: PostViewHolder
    private lateinit var inputData: Post
    private lateinit var binding: FragmentFullscreenPostBinding

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inputData = arguments?.postData!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullscreenPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postHolder = PostViewHolder(binding.postLayout, object : ClickCallback {
            override fun onOpenClick(position: Int) {
                //no use
            }

            override fun onLikeClick(position: Int) {
                viewModel.likeById(inputData.id, inputData.likedByMe)
            }

            override fun onShareClick(position: Int) {
                //viewModel.shareById(inputData.id)
            }

            override fun onRemoveClick(position: Int) {
                viewModel.removeById(inputData.id)
            }

            override fun onEditClick(position: Int) {
                viewModel.edit(inputData)
            }

            override fun onYoutubeLinkClick(position: Int) {
                inputData.openYoutube(requireActivity())
            }
        })
        postHolder.bind(inputData)
        viewModel.data.observe(viewLifecycleOwner, { feedModel ->
            feedModel.posts.find {
                it.id == inputData.id
            }?.let {
                inputData = it
                postHolder.bind(inputData)
            } ?: findNavController().navigateUp()
        })

        viewModel.editPost.observe(viewLifecycleOwner, { post ->
            if (post.id != 0L) {
                navigate(R.id.action_fullscreenPostFragment_to_changePostFragment, post)
            }
        })
    }

    companion object {
        var Bundle.postData: Post? by PostDataArg
    }
}