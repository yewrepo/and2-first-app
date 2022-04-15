package ru.netology

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.db.PostEntity
import ru.netology.db.PostRemoteKeyEntity
import ru.netology.db.dao.Converters
import ru.netology.db.dao.PostDao
import ru.netology.db.dao.PostRemoteKeyDao

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}