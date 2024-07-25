package com.diklat.tanyapakar.ui.materi

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.diklat.tanyapakar.core.data.source.model.Log
import com.diklat.tanyapakar.core.data.source.model.Materi
import com.diklat.tanyapakar.core.data.source.repository.Repository
import com.diklat.tanyapakar.core.util.ViewEvents
import com.diklat.tanyapakar.core.util.ViewEventsMateri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class MateriViewModel @Inject constructor(private val repository: Repository):ViewModel() {

//    fun getPagingMateri() = repository.getPagingMateri().asLiveData()

    suspend fun getDetailMateri(data:String) = repository.getDetailMateri(data).asLiveData()

    private lateinit var modificationEvents: MutableStateFlow<List<ViewEventsMateri>>
    val pagingData = MutableLiveData<LiveData<PagingData<Materi>>>()

    fun getData() {
        modificationEvents = MutableStateFlow(emptyList())
        pagingData.value = repository.getPagingMateri()
            .cachedIn(viewModelScope)
            .combine(modificationEvents) { pagingData, modifications ->
                modifications.fold(pagingData) { acc, event ->
                    applyEvents(acc, event)
                }
            }.asLiveData()
    }

    private fun applyEvents(
        paging: PagingData<Materi>,
        viewEvents: ViewEventsMateri
    ): PagingData<Materi> {
        return when (viewEvents) {
            is ViewEventsMateri.Remove -> {
                paging
                    .filter { viewEvents.entity.id_materi != it.id_materi }
            }
        }
    }

    suspend fun uploadMateri(data: Materi, file: Uri) = repository.uploadMateri(data,file).asLiveData()

    suspend fun deleteMateri(data: Materi) = repository.deleteMateri(data).asLiveData()

    fun onViewEvent(sampleViewEvents: ViewEventsMateri) {
        modificationEvents.value += sampleViewEvents
    }
}