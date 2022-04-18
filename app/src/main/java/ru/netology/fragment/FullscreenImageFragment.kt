package ru.netology.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.netology.extension.PostDataArg
import ru.netology.extension.getRemoteMediaRoute
import ru.netology.model.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFullscreenImageBinding
import ru.netology.vm.PostViewModel

class FullscreenImageFragment : Fragment() {

    private lateinit var post: Post
    private lateinit var binding: FragmentFullscreenImageBinding

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        post = requireArguments().postData!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullscreenImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.removePhoto.isVisible = false
        binding.removePhoto.setOnClickListener {
            viewModel.removePhoto()
            findNavController().navigateUp()
        }

        val loadingSource = if (post.photoModel != null) {
            binding.removePhoto.isVisible = true
            post.photoModel?.uri
        } else {
            binding.removePhoto.isVisible = false
            post.attachment?.getRemoteMediaRoute()
        }

        Glide.with(requireContext())
            .load(loadingSource)
            .timeout(10_000)
            .placeholder(R.drawable.ic_broken_image_24dp)
            .fitCenter()
            .into(binding.photoLayoutPreview)
    }

    companion object {
        var Bundle.postData: Post? by PostDataArg
    }
}