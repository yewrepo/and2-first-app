package ru.netology.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.datasource.PostDataSource
import ru.netology.datasource.RetrofitPostSourceImpl
import ru.netology.datasource.RoomPostSourceImpl
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RetrofitPostSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RoomPostSource

@Module
@InstallIn(SingletonComponent::class)
interface DataSourcesModule {
    @Binds
    @RetrofitPostSource
    fun provideMyFirstClass(clazz: RetrofitPostSourceImpl): PostDataSource

    @Binds
    @RoomPostSource
    fun provideMySecondClass(clazz: RoomPostSourceImpl): PostDataSource
}