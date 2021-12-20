package ru.netology.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.extension.likeOrNot
import ru.netology.extension.shareMe
import ru.netology.nmedia.Post
import kotlin.random.Random

class PostRepositoryMemoryImpl : PostRepository {

    private var post = Post(
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

    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data

    override fun like() {
        post = post.likeOrNot()
        data.value = post
    }

    override fun share() {
        post = post.shareMe()
        data.value = post
    }

    private fun generateRandom(min: Int, max: Int): Int {
        return Random(System.currentTimeMillis()).nextInt(min, max)
    }
}