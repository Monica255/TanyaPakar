package com.diklat.tanyapakar.core.data.source.model

data class Log(
    var id_log:String?=null,
    val description:String?=null,
    var file:String?=null,
    val file_name:String?=null,
    val id_tenant:String?=null,
    val id_user:String?=null,
    val timestamp:Long?=null
)
