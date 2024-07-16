package com.diklat.tanyapakar.core.data.source.repository

import androidx.lifecycle.MutableLiveData
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.firebase.FirebaseDataSource
import com.diklat.tanyapakar.core.data.source.model.Chat
import com.diklat.tanyapakar.core.data.source.model.ChatMessage
import com.diklat.tanyapakar.core.data.source.model.Expertise
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
    fun getChats(id:String, role:String) = firebaseDataSource.getChats(id,role)

    suspend fun getChatId(idPakar:String, idTenant: String):Chat? = firebaseDataSource.getChatId(idPakar,idTenant)

    fun getChatMessages(idChat:String): MutableLiveData<List<ChatMessage>?> = firebaseDataSource.getChatMessages(idChat)

    fun readMessage(chatId:String) = firebaseDataSource.readMessage(chatId)

    suspend fun getUserIDbyRoleId(roleID:String):String? = firebaseDataSource.getUserIDbyRoleId(roleID)

    suspend fun sendChat(chatID: String, data: ChatMessage,bot:Boolean,updateStatus:Boolean): MutableLiveData<Resource<String>> = firebaseDataSource.sendChat(chatID,data,bot,updateStatus)
}