package com.diklat.tanyapakar.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diklat.tanyapakar.MyPreference
import com.diklat.tanyapakar.core.data.source.firebase.LoginType
import com.diklat.tanyapakar.core.data.source.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val myPreference: MyPreference, private val authRepository: AuthRepository):ViewModel() {

    suspend fun login(emailNumber: String,loginType: LoginType)= authRepository.login(emailNumber,loginType).asLiveData()

    suspend fun getUserData(id:String) = authRepository.getUserData(id).asLiveData()

    fun getToken()=myPreference.getToken().asLiveData()


    fun saveToken(token: String) {
        viewModelScope.launch {
            myPreference.saveToken(token)
        }
    }
}