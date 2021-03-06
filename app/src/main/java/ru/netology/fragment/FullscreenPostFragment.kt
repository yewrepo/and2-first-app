package ru.netology.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.adapter.ClickCallback
import ru.netology.adapter.PostViewHolder
import ru.netology.extension.PostDataArg
import ru.netology.extension.navigate
import ru.netology.extension.openYoutube
import ru.netology.model.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFullscreenPostBinding
import ru.netology.repository.PostDataRepository
import ru.netology.vm.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FullscreenPostFragment : Fragment() {

    @Inject
    lateinit var repository: PostDataRepository

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

            override fun onPhotoOpenClick(position: Int) {
                navigate(
                    R.id.action_fullscreenPostFragment_to_fullscreenImageFragment,
                    post = inputData
                )
            }

            override fun onAdOpenClick(position: Int) {
                //not use
            }
        })
        postHolder.bind(inputData)

        viewModel.editPost.observe(viewLifecycleOwner) { post ->
            if (post.id != 0L) {
                navigate(R.id.action_fullscreenPostFragment_to_changePostFragment, post)
            }
        }
    }

    companion object {
        var Bundle.postData: Post? by PostDataArg
    }
}