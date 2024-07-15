package com.diklat.tanyapakar.core.data.source.model

data class Members(
    val pakar:String?=null,
    val tenant:String?=null
)
data class Chat(
    var id_chat:String ="",
    val lastChat:String? = null,
    val lastChatStatus:String? = null,
    val lastTimestamp:Long? = null,
    val members: Members? =null,
    val lastMessage:String? = null
)
