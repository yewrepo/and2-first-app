package ru.netology

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.db.PostEntity
import ru.netology.db.dao.Converters
import ru.netology.db.dao.PostDao

@Database(entities = [PostEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao

    companion object {

        @Volatile
        private var instance: AppDb? = null

        fun getInstance(c: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(c).also { instance = it }
            }
        }

        private fun buildDatabase(c: Context) =
            Room.databaseBuilder(c, AppDb::class.java, "app.db")
                .allowMainThreadQueries()
                .build()
    }
}