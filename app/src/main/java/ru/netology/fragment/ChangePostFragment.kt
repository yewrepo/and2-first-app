package ru.netology.fragment

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.extension.PostDataArg
import ru.netology.extension.hideKeyboard
import ru.netology.extension.navigate
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentChangePostBinding
import ru.netology.vm.PostViewModel

@AndroidEntryPoint
class ChangePostFragment : Fragment() {

    private lateinit var post: Post
    private lateinit var binding: FragmentChangePostBinding

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val pickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                ImagePicker.RESULT_ERROR -> {
                    Snackbar.make(
                        binding.root,
                        ImagePicker.getError(it.data),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                Activity.RESULT_OK -> {
                    val uri: Uri? = it.data?.data
                    viewModel.changePhoto(uri, uri?.toFile())
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        post = requireArguments().postData!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.save.setOnClickListener {
            if (getInputtedText().isEmpty()) {
                Toast.makeText(
                    binding.root.context,
                    R.string.error_empty_context,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.changeContent(getInputtedText(), getInputtedLinkText())
                viewModel.save()
                requireView().hideKeyboard()
            }
        }
        binding.cancel.setOnClickListener {
            viewModel.cancel()
            findNavController().navigateUp()
        }
        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickerLauncher::launch)

        }
        binding.selectPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpg"
                    )
                )
                .createIntent(pickerLauncher::launch)
        }
        binding.removePhoto.setOnClickListener {
            viewModel.removePhoto()
        }
        binding.photoLayoutPreview.setOnClickListener {
            navigate(
                R.id.action_changePostFragment_to_fullscreenImageFragment,
                post = post
            )
        }

        viewModel.editPost.observe(viewLifecycleOwner) { post ->
            binding.content.setText(post?.content.orEmpty())
            binding.videoLink.setText(post?.youtubeLink.orEmpty())
            binding.previewCard.isVisible = post?.photoModel != null
            post?.photoModel?.apply {
                Glide.with(requireContext())
                    .load(uri)
                    .timeout(10_000)
                    .placeholder(R.drawable.ic_broken_image_24dp)
                    .centerCrop()
                    .into(binding.photoLayoutPreview)
            }
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun getInputtedText() = binding.content.text?.toString().orEmpty()

    private fun getInputtedLinkText() = binding.videoLink.text?.toString().orEmpty()

    companion object {
        var Bundle.postData: Post? by PostDataArg
    }
}