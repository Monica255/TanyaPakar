package com.diklat.tanyapakar.core.data.source.model

data class Pakar(
    val email: String? = null,
    val id_pakar: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val expertise: List<String>? = null,
    val profile_img: String? = null,
    val jabatan_struktural: String? = null,
    val tmt_struktural: String? = null,
    val jabatan_fungsional: String? = null,
    val tmt_fungsional: String? = null,
    val last_education: String? = null
)

data class Expertise(
    val id_expertise: String = "",
    val description: String? = null,
    val name: String? = null
)