package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.extension.shortFormat
import ru.netology.extension.likeOrNot
import ru.netology.extension.shareMe
import ru.netology.nmedia.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var post: Post
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. " +
                    "Затем появились курсы по дизайну, разработке, аналитике и управлению. " +
                    "Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. " +
                    "Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше," +
                    " целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен " +
                    "→ http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likes = generateRandom(100, 1000),
            share = generateRandom(100, 200),
            view = generateRandom(1000, 150000)
        )
        setCallbacks()
        showPost(post)
    }

    private fun setCallbacks() {
        binding.likes.setOnClickListener {
            post = post.likeOrNot()
            showPost(post)
        }
        binding.share.setOnClickListener {
            post = post.shareMe()
            showPost(post)
        }
    }

    private fun showPost(post: Post) {
        setViews(post)
        setShare(post)
        setLikes(post)
        with(binding) {
            author.text = post.author
            content.text = post.content
            published.text = post.published
        }
    }

    private fun setViews(post: Post) {
        binding.views.text = post.view.shortFormat()
    }

    private fun setShare(post: Post) {
        binding.share.text = post.share.shortFormat()
    }

    private fun setLikes(post: Post) {
        binding.likes.text = post.likes.shortFormat()
        val resId =
            if (post.likedByMe) R.drawable.ic_fill_liked_24dp else R.drawable.ic_fill_like_24dp
        binding.likes.setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0)
    }

    private fun generateRandom(min: Int, max: Int): Int {
        return Random(System.currentTimeMillis()).nextInt(min, max)
    }
}
