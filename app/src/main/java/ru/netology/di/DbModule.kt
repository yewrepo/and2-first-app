package ru.netology.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.AppDb
import ru.netology.datasource.PostDataSource
import ru.netology.datasource.RoomPostSourceImpl
import ru.netology.db.dao.PostRemoteKeyDao

@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Provides
    fun provideDb(@ApplicationContext c: Context): AppDb {
        return Room.databaseBuilder(c, AppDb::class.java, "app.db").build()
    }

    @Provides
    fun providePostDao(db: AppDb) = db.postDao()

    @Provides
    fun provideLocalSource(appDb: AppDb): PostDataSource = RoomPostSourceImpl(appDb.postDao())

    @Provides
    fun providePostRemoteKeyDao(appDb: AppDb): PostRemoteKeyDao = appDb.postRemoteKeyDao()
}