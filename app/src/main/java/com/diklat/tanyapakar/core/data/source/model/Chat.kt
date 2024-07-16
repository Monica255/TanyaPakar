package com.diklat.tanyapakar.core.data.source.model

data class Members(
    val pakar:String?=null,
    val tenant:String?=null
)
data class Chat(
    var id_chat:String ="",
    val chatStatus:String?=null,
    val lastChat:String? = null,
    val lastChatStatus:String? = null,
    val lastTimestamp:Long? = null,
    val members: Members? =null,
    val lastMessage:String? = null,
    val numberChatDone:Int = 0
)

data class ChatMessage(
    var id_messages: String?=null,
    val sentBy:String?=null,
    val message: String="",
    val timestamp: Long=0
)
