package com.diklat.tanyapakar.core.injection

import com.diklat.tanyapakar.core.data.source.firebase.FirebaseDataSource
import com.diklat.tanyapakar.core.data.source.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [FirebaseModule::class])
@InstallIn(SingletonComponent::class)
class RepositoryModule {

//    @Provides
//    @Singleton
//    fun provideBibleRepository(@DefaultBaseUrl defaultApiService: ApiService): BibleRepository{
//        return BibleRepository(defaultApiService)
//    }


    @Provides
    @Singleton
    fun provideAuthRepository(firebaseDataSource: FirebaseDataSource): AuthRepository = AuthRepository(firebaseDataSource)
}