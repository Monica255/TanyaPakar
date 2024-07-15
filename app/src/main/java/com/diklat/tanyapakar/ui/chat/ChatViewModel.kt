package com.diklat.tanyapakar.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.diklat.tanyapakar.core.data.source.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: Repository):ViewModel() {
//    val getPaginatedChats = repository.getPaginatedChats().asLiveData()
    fun getChats(id:String,role:String) = repository.getChats(id,role)
}