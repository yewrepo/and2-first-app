package ru.netology.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.extension.likeOrNot
import ru.netology.extension.shareMe
import ru.netology.nmedia.Post
import kotlin.random.Random

class PostRepositoryMemoryImpl : PostRepository {

    private var posts: MutableList<Post> = mutableListOf(
        Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. " +
                    "Затем появились курсы по дизайну, разработке, аналитике и управлению. " +
                    "Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. " +
                    "Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше," +
                    " целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен " +
                    "→ http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likes = generateRandom(1, 100, 1000),
            share = generateRandom(1, 100, 200),
            view = generateRandom(1, 1000, 150000)
        ),
        Post(
            id = 2,
            author = "Пост 2",
            content = "Тест поста 2",
            published = "21 мая в 18:36",
            likes = generateRandom(2, 100, 1000),
            share = generateRandom(2, 100, 200),
            view = generateRandom(2, 1000, 150000)
        ),
        Post(
            id = 3,
            author = "Пост 3",
            content = "Тест поста 3",
            published = "21 мая в 18:36",
            likes = generateRandom(3, 100, 1000),
            share = generateRandom(3, 100, 200),
            view = generateRandom(3, 1000, 150000)
        )
    )
    private var nextId: Int = posts.size + 1

    private val data = MutableLiveData(posts)

    override fun get(): LiveData<MutableList<Post>> = data

    override fun likeById(id: Int) {
        posts.map { p ->
            if (p.id == id) {
                p.likeOrNot()
            } else {
                p
            }
        }.apply {
            posts = this.toMutableList()
            data.value = this.toMutableList()
        }
    }

    override fun shareById(id: Int) {
        posts.map { p ->
            if (p.id == id) {
                p.shareMe()
            } else {
                p
            }
        }.apply {
            posts = this.toMutableList()
            data.value = this.toMutableList()
        }
    }

    override fun removeById(id: Int) {
        posts.filter { p ->
            p.id != id
        }.apply {
            posts = this.toMutableList()
            data.value = this.toMutableList()
        }
    }

    override fun save(post: Post) {
        if (post.id == 0) {
            posts =
                (mutableListOf(
                    post.copy(
                        id = nextId++,
                        author = "Me",
                        published = "now"
                    )
                ) + posts) as MutableList<Post>
        } else {
            posts = posts.map { p ->
                if (p.id == post.id) {
                    p.copy(content = post.content)
                } else {
                    p
                }
            }.toMutableList()
        }
        data.value = posts
    }

    private fun generateRandom(seed: Int, min: Int, max: Int): Int {
        return Random(seed).nextInt(min, max)
    }
}