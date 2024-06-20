package com.diklat.tanyapakar.core.data.source.repository

import android.content.Context
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.firebase.FirebaseDataSource
import com.diklat.tanyapakar.core.data.source.firebase.LoginType
import com.diklat.tanyapakar.core.data.source.model.UserData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine



class AuthRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource,
) {
    suspend fun login(emailNumber: String,loginType: LoginType) = firebaseDataSource.login(emailNumber,loginType)

    suspend fun getUserData(
        id: String
    ) = firebaseDataSource.getUserData(id)
}