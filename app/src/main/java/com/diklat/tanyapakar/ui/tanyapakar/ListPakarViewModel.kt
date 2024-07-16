package com.diklat.tanyapakar.ui.tanyapakar

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.model.Expertise
import com.diklat.tanyapakar.core.data.source.model.Pakar
import com.diklat.tanyapakar.core.data.source.repository.Repository
import com.diklat.tanyapakar.core.util.ViewEventsPakar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class ListPakarViewModel@Inject constructor(private val repository: Repository) :ViewModel(){
    var mExp: Expertise? = null
    val pagingData = MutableLiveData<LiveData<PagingData<Pakar>>>()
    var expertise = MutableLiveData<MutableList<Expertise>>()
    fun getData(exp: Expertise? = mExp,self:Boolean=false) {
        if (mExp != exp) mExp = exp
        Log.d("error pakar",exp.toString())
        pagingData.value = repository.getPagingPakar(mExp).asLiveData()

    }
    suspend fun getListExpertise() = repository.getListExpertise().asLiveData()

    suspend fun getDetailPakar(data: String) = repository.getDetailPakar(data).asLiveData()

    suspend fun getExpertise(expertise: List<String>) = repository.getExpertise(expertise).asLiveData()

    suspend fun getUserIDbyRoleId(roleID:String):String?=repository.getUserIDbyRoleId(roleID)
}