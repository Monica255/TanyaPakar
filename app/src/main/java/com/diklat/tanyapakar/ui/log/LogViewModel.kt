package com.diklat.tanyapakar.ui.log

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.model.Log
import com.diklat.tanyapakar.core.data.source.repository.Repository
import com.diklat.tanyapakar.core.util.ViewEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(private val repository: Repository) :ViewModel(){
    private lateinit var modificationEvents: MutableStateFlow<List<ViewEvents>>
    val pagingData = MutableLiveData<LiveData<PagingData<Log>>>()

    fun getData(id_user:String) {
        modificationEvents = MutableStateFlow(emptyList())
        pagingData.value = repository.getPagingLog(id_user)
            .cachedIn(viewModelScope)
            .combine(modificationEvents) { pagingData, modifications ->
                modifications.fold(pagingData) { acc, event ->
                    applyEventsForumPost(acc, event)
                }
            }.asLiveData()
    }

    private fun applyEventsForumPost(
        paging: PagingData<Log>,
        ViewEvents: ViewEvents
    ): PagingData<Log> {
        return when (ViewEvents) {
            is ViewEvents.Remove -> {
                paging
                    .filter { ViewEvents.entity.id_log != it.id_log }
            }
        }
    }

    suspend fun getDetailLog(data: String)=repository.getDetailLog(data).asLiveData()

    suspend fun uploadLog(data: Log, file: Uri) = repository.uploadLog(data,file).asLiveData()

    suspend fun deleteLog(data: Log) = repository.deleteLog(data).asLiveData()

    fun onViewEvent(sampleViewEvents: ViewEvents) {
        modificationEvents.value += sampleViewEvents
    }
}