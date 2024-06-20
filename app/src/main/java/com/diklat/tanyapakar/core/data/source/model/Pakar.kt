package com.diklat.tanyapakar.core.data.source.model

data class Pakar(
    val email: String? = null,
    val id_pakar: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val expertise: List<String>? = null,
    val profile_img: String? = null
)
