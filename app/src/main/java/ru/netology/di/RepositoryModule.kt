package ru.netology.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.repository.PostDataRepository
import ru.netology.repository.RetrofitPostRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideRepository(impl: RetrofitPostRepositoryImpl): PostDataRepository
}