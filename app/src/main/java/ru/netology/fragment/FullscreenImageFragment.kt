package ru.netology.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFullscreenImageBinding
import ru.netology.vm.PostViewModel

class FullscreenImageFragment : Fragment() {

    private lateinit var binding: FragmentFullscreenImageBinding

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

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

        binding.removePhoto.setOnClickListener {
            viewModel.removePhoto()
            findNavController().navigateUp()
        }

        viewModel.editPost.observe(viewLifecycleOwner, { post ->
            post.photoModel?.apply {
                Glide.with(requireContext())
                    .load(uri)
                    .timeout(10_000)
                    .placeholder(R.drawable.ic_broken_image_24dp)
                    .centerCrop()
                    .into(binding.photoLayoutPreview)
            }
        })
    }

}