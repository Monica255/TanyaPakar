package com.diklat.tanyapakar.core.util

import com.diklat.tanyapakar.core.data.source.model.Log
import com.diklat.tanyapakar.core.data.source.model.Pakar

const val EXTRA_ID ="extra_id"
const val CHAT_ID = "chat_id"
const val CHAT_STATUS = "chat_status"
const val PAKAR_ID = "pakar_id"
const val USER_PAKAR_ID = "user_pakar_id"
const val NAME = "name"
const val PHOTO = "photo"
sealed class ViewEvents {
    data class Remove(val entity: Log) : ViewEvents()
}