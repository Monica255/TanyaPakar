package com.diklat.tanyapakar.core.data.source.model

data class Materi(
    var id_materi:String?=null,
    val title:String?=null,
    var file:String?=null,
    val file_name:String?=null,
    val description:String?=null,
    val id_user:String?=null,
    val timestamp:Long?=null
)
