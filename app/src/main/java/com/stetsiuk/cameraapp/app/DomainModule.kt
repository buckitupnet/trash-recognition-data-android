package com.stetsiuk.cameraapp.app

import android.content.Context
import com.stetsiuk.cameraapp.domain.JsonRepository
import com.stetsiuk.cameraapp.repo.JsonRepositoryImpl
import com.stetsiuk.cameraapp.repo.TransformImageUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    @Singleton
    @Provides
    fun providesCollectData(@ApplicationContext appContext: Context) : com.stetsiuk.cameraapp.domain.TransformImageUseCase {
        return TransformImageUseCaseImpl(providesJsonRepository(appContext))
    }

    @Singleton
    @Provides
    fun providesJsonRepository(@ApplicationContext appContext: Context) : JsonRepository {
        return JsonRepositoryImpl(appContext)
    }
}