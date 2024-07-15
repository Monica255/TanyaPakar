package com.diklat.tanyapakar.ui.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.diklat.tanyapakar.core.data.source.model.ChatMessage
import com.diklat.tanyapakar.core.data.source.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: Repository):ViewModel() {
//    val getPaginatedChats = repository.getPaginatedChats().asLiveData()
    fun getChats(id:String,role:String) = repository.getChats(id,role)
    suspend fun getChatId(idPakar:String, idTenant: String):String? = repository.getChatId(idPakar,idTenant)
    fun getChats(idChat:String): MutableLiveData<List<ChatMessage>?> = repository.getChats(idChat)
    fun readMessage(chatId:String) = repository.readMessage(chatId)
}