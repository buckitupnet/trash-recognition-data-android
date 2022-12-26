package com.siabucketup.ecotaxi.app

import android.content.Context
import com.siabucketup.ecotaxi.domain.JsonRepository
import com.siabucketup.ecotaxi.repo.JsonRepositoryImpl
import com.siabucketup.ecotaxi.repo.TransformImageUseCaseImpl
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
    fun providesCollectData(@ApplicationContext appContext: Context) : com.siabucketup.ecotaxi.domain.TransformImageUseCase {
        return TransformImageUseCaseImpl(providesJsonRepository(appContext))
    }

    @Singleton
    @Provides
    fun providesJsonRepository(@ApplicationContext appContext: Context) : JsonRepository {
        return JsonRepositoryImpl(appContext)
    }
}