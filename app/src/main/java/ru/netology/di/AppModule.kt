package ru.netology.di

import com.google.android.gms.common.GoogleApiAvailabilityLight
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideGoogleApi(): GoogleApiAvailabilityLight = GoogleApiAvailabilityLight.getInstance()
}