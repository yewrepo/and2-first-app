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

@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Provides
    fun provideDb(@ApplicationContext c: Context): AppDb {
        return Room.databaseBuilder(c, AppDb::class.java, "app.db").build()
    }

    @Provides
    fun provideLocalSource(appDb: AppDb): PostDataSource = RoomPostSourceImpl(appDb.postDao())
}