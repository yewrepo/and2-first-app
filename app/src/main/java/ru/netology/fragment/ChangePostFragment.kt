package ru.netology.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.extension.PostDataArg
import ru.netology.extension.hideKeyboard
import ru.netology.nmedia.ChangePostData
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentChangePostBinding
import ru.netology.vm.PostViewModel

class ChangePostFragment : Fragment(R.layout.fragment_change_post) {

    private var inputData: ChangePostData? = null
    private lateinit var binding: FragmentChangePostBinding

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
        binding = FragmentChangePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.content.setText(inputData?.text.orEmpty())

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
                findNavController().navigateUp()
            }
        }
        binding.cancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getInputtedText() = binding.content.text?.toString().orEmpty()

    private fun getInputtedLinkText() = binding.videoLink.text?.toString().orEmpty()

    companion object {
        var Bundle.postData: ChangePostData? by PostDataArg
    }
}