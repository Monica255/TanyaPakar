package com.diklat.tanyapakar.ui.materi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.diklat.tanyapakar.core.data.source.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MateriViewModel @Inject constructor(private val repository: Repository):ViewModel() {

    fun getPagingMateri() = repository.getPagingMateri().asLiveData()

    suspend fun getDetailMateri(data:String) = repository.getDetailMateri(data).asLiveData()
}