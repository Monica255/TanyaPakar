package com.diklat.tanyapakar.ui.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.model.Chat
import com.diklat.tanyapakar.core.data.source.model.ChatMessage
import com.diklat.tanyapakar.core.data.source.model.Log
import com.diklat.tanyapakar.core.data.source.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    //    val getPaginatedChats = repository.getPaginatedChats().asLiveData()
    fun getChats(id: String, role: String) = repository.getChats(id, role)
    suspend fun getChatId(idPakar: String, idTenant: String): Chat? =
        repository.getChatId(idPakar, idTenant)

    fun getChatMessages(idChat: String): MutableLiveData<List<ChatMessage>?> =
        repository.getChatMessages(idChat)

    fun readMessage(chatId: String) = repository.readMessage(chatId)
    suspend fun sendChat(
        chatID: String,
        data: ChatMessage,
        updates: HashMap<String, Any>?
    ): MutableLiveData<Resource<String>> = repository.sendChat(chatID, data, updates)

    fun getChatData(chatId: String): MutableLiveData<Chat?> = repository.getChatData(chatId)

    suspend fun getForm():String? = repository.getForm()


}