package com.diklat.tanyapakar.ui.galery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.diklat.tanyapakar.core.data.source.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GaleryViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    suspend fun getGalery() = repository.getGallery().asLiveData()
    fun getPaginatedGalleryUrls() = repository.getPaginatedGalleryUrls().asLiveData()
}