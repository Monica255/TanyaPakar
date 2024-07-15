package com.diklat.tanyapakar.core.data.source.repository

import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.firebase.FirebaseDataSource
import com.diklat.tanyapakar.core.data.source.model.Expertise
import com.diklat.tanyapakar.core.data.source.model.Materi
import com.diklat.tanyapakar.core.data.source.model.Pakar
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    fun getPagingPakar(exp:Expertise?) = firebaseDataSource.getPagingPakar(exp)

    fun getPagingMateri() = firebaseDataSource.getPagingMateri()

    suspend fun getListExpertise() = firebaseDataSource.getListExpertise()

    suspend fun getDetailPakar(data: String) = firebaseDataSource.getDetailPakar(data)

    suspend fun getExpertise(expertise: List<String>) = firebaseDataSource.getExpertise(expertise)

    suspend fun getDetailMateri(data: String) = firebaseDataSource.getDetailMateri(data)

    suspend fun getGallery()= firebaseDataSource.getGallery()

    fun getPaginatedGalleryUrls() = firebaseDataSource.getPaginatedGalleryUrls()
    
//    fun getPaginatedChats() = firebaseDataSource.getPaginatedChats()
    fun getChats(id:String,role:String) = firebaseDataSource.getChats(id,role)
}