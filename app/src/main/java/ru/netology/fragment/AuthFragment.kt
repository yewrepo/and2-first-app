package ru.netology.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.model.Error
import ru.netology.nmedia.R
import ru.netology.model.Success
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.vm.AuthViewModel

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var hasLogin: Boolean = false
    private var hasPassword: Boolean = false
    private val viewModel: AuthViewModel by activityViewModels()

    private var afterStartup = false

    private lateinit var binding: FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadingState.observe(viewLifecycleOwner) { state ->
            if (afterStartup) {
                when (state) {
                    Error -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.auth_error_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Success -> {
                        findNavController().navigateUp()
                    }
                }
            }
            afterStartup = true
        }

        binding.loginText.addTextChangedListener {
            hasLogin = it.isNullOrBlank().not()
            updateButtonState()
        }

        binding.passwordText.addTextChangedListener {
            hasPassword = it.isNullOrBlank().not()
            updateButtonState()
        }

        binding.actionButton.setOnClickListener {
            viewModel.authUser(
                binding.loginText.text!!.toString(),
                binding.passwordText.text!!.toString(),
            )
        }
    }

    private fun updateButtonState() {
        binding.actionButton.isEnabled = hasLogin && hasPassword
    }
}