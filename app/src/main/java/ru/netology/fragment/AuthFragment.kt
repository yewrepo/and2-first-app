package ru.netology.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.vm.AuthViewModel

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var hasLogin: Boolean = false
    private var hasPassword: Boolean = false
    private val viewModel: AuthViewModel by activityViewModels()

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