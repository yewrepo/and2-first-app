package ru.netology.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.AppDb
import ru.netology.datasource.PostDataSource
import ru.netology.datasource.RoomPostSourceImpl
import ru.netology.db.dao.PostRemoteKeyDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext c: Context): AppDb {
        return Room.databaseBuilder(c, AppDb::class.java, "app.db")
            .setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun providePostDao(db: AppDb) = db.postDao()

    @Singleton
    @Provides
    fun provideLocalSource(appDb: AppDb): PostDataSource = RoomPostSourceImpl(appDb.postDao())

    @Singleton
    @Provides
    fun providePostRemoteKeyDao(appDb: AppDb): PostRemoteKeyDao = appDb.postRemoteKeyDao()
}