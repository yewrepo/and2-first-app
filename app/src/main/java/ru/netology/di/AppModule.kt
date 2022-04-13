package ru.netology.di

import com.google.android.gms.common.GoogleApiAvailabilityLight
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.AppDb
import ru.netology.db.dao.PostDao
import ru.netology.db.dao.PostRemoteKeyDao
import ru.netology.network.PostAPI
import ru.netology.repository.PostRemoteMediator
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideGoogleApi(): GoogleApiAvailabilityLight = GoogleApiAvailabilityLight.getInstance()

    @Singleton
    @Provides
    fun provideMediator(
        postApi: PostAPI,
        appDb: AppDb,
        postDao: PostDao,
        postRemoteKeyDao: PostRemoteKeyDao
    ): PostRemoteMediator = PostRemoteMediator(postApi, appDb, postDao, postRemoteKeyDao)
}