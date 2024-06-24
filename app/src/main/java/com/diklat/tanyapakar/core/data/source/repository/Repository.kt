package com.diklat.tanyapakar.core.data.source.repository

import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.firebase.FirebaseDataSource
import com.diklat.tanyapakar.core.data.source.model.Expertise
import com.diklat.tanyapakar.core.data.source.model.Pakar
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    fun getPagingPakar(exp:Expertise?) = firebaseDataSource.getPagingPakar(exp)

    suspend fun getListExpertise() = firebaseDataSource.getListExpertise()

    suspend fun getDetailPakar(data: String) = firebaseDataSource.getDetailPakar(data)
}