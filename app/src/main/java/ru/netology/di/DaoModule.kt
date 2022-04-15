package ru.netology.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.AppDb

@InstallIn(SingletonComponent::class)
@Module
class DaoModule {

    @Provides
    fun providePostDao(db: AppDb) = db.postDao()
}