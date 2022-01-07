package ru.netology.nmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ru.netology.nmedia.databinding.ActivityChangePostBinding

class ChangePostActivity : AppCompatActivity() {

    private lateinit var inputData: ChangePostData
    private lateinit var binding: ActivityChangePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputData =
            intent.getParcelableExtra(ChangePostResultContract.extraKey)!!

        val titleId = if (inputData.id == 0) R.string.title_create else R.string.title_edit
        supportActionBar?.title = getString(titleId)

        binding.content.setText(inputData.text.orEmpty())

        binding.save.setOnClickListener {
            if (getInputtedText().isEmpty()) {
                Toast.makeText(
                    binding.root.context,
                    R.string.error_empty_context,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                setResult(RESULT_OK, getResultIntent())
                finish()
            }
        }
        binding.cancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun getResultIntent(): Intent {
        return Intent().also {
            it.putExtra(
                ChangePostResultContract.extraKey,
                inputData.copy(text = getInputtedText())
            )
        }
    }

    private fun getInputtedText() = binding.content.text?.toString().orEmpty()
}