package com.diklat.tanyapakar.core.data.source.firebase

import GalleryPagingSource
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.model.Chat
import com.diklat.tanyapakar.core.data.source.model.ChatMessage
import com.diklat.tanyapakar.core.data.source.model.Expertise
import com.diklat.tanyapakar.core.data.source.model.Materi
import com.diklat.tanyapakar.core.data.source.model.Members
import com.diklat.tanyapakar.core.data.source.model.Pakar
import com.diklat.tanyapakar.core.data.source.model.Tenant
import com.diklat.tanyapakar.core.data.source.model.UserData
import com.example.tanyapakar.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class LoginType(val printable: String) {
    EMAIL("email"), PHONE("phone")
}

enum class Role(val printable: String) {
    TENANT("tenant"), ADMIN("admin"), PAKAR("pakar"), GUESS("guess")
}

@Singleton
class FirebaseDataSource @Inject constructor(
    firebaseDatabase: FirebaseDatabase,
//    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    firebaseFirestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) {

    private lateinit var listenerChats: ValueEventListener
    private lateinit var listenerChatData: ValueEventListener
    private lateinit var listenerChatMessages: ValueEventListener
    private val tenantRef = firebaseFirestore.collection("tenant")
    private val pakarRef = firebaseFirestore.collection("pakar")
    private val userRef = firebaseFirestore.collection("user")
    private val expertiseRef = firebaseFirestore.collection("expertise")
    private val materiRef = firebaseFirestore.collection("materi")
    private val galeryRef = firebaseStorage.reference.child("gallery")
    private val chatRef = firebaseDatabase.reference.child("chats/")
    private val chatMessageRef = firebaseDatabase.reference.child("chatMessages/")
    private val formRef = firebaseDatabase.reference.child("evaluationForm")

    suspend fun login(
        emailNumber: String,
        loginType: LoginType
    ): Flow<Resource<String?>> {
        return flow {
            emit(Resource.Loading())
            try {
                val result = suspendCoroutine<Task<QuerySnapshot>> { continuation ->
                    userRef.whereEqualTo(
                        if (loginType == LoginType.PHONE) "phone" else "email",
                        emailNumber
                    ).limit(1).get().addOnCompleteListener { task ->
                        continuation.resume(task)
                    }
                }

                val data =
                    result.result?.documents

                if (!data.isNullOrEmpty()) {
                    val data = data?.get(0)?.toObject<UserData>()
                    emit(Resource.Success(data!!.id_user!!))
                    return@flow
                }

                //get from tenant
                val result2 = suspendCoroutine<Task<QuerySnapshot>> { continuation ->
                    tenantRef.whereEqualTo(
                        if (loginType == LoginType.PHONE) "phone" else "email",
                        emailNumber
                    ).limit(1).get().addOnCompleteListener { task ->
                        continuation.resume(task)
                    }
                }

                val data2 =
                    result2.result?.documents

                if (!data2.isNullOrEmpty()) {
                    data2?.get(0)?.toObject<Tenant>()?.let {
                        val id = saveUserData(
                            UserData(
                                null,
                                it.email,
                                it.phone,
                                Role.TENANT.printable,
                                it.id_tenant,
                                it.name
                            )
                        )
                        emit(Resource.Success(id))
                        return@flow
                    }
                }

                //get from pakar
                val result3 = suspendCoroutine<Task<QuerySnapshot>> { continuation ->
                    pakarRef.whereEqualTo(
                        if (loginType == LoginType.PHONE) "phone" else "email",
                        emailNumber
                    ).limit(1).get().addOnCompleteListener { task ->
                        continuation.resume(task)
                    }
                }

                val data3 =
                    result3.result?.documents

                if (!data3.isNullOrEmpty()) {
                    data3?.get(0)?.toObject<Pakar>()?.let {
                        val id = saveUserData(
                            UserData(
                                null,
                                it.email,
                                it.phone,
                                Role.PAKAR.printable,
                                it.id_pakar,
                                it.name
                            )
                        )
                        emit(Resource.Success(id))
                        return@flow
                    }
                }

                emit(Resource.Success(null))

            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    suspend fun saveUserData(data: UserData): String? {
        val key = userRef.document().id
        if (data.id_user == null) data.id_user = key
        val result = suspendCoroutine<Task<Void>> { continuation ->
            userRef.document(key).set(data)
                .addOnCompleteListener { task ->
                    continuation.resume(task)
                }
        }
        if (result.isSuccessful) {
            return key
        } else {
            return null
        }

    }

    suspend fun getDetailPakar(data: String): Flow<Resource<Pakar>> {
        return flow {
            emit(Resource.Loading())
            val result = suspendCoroutine<Task<DocumentSnapshot>> { continuation ->
                pakarRef.document(data).get()
                    .addOnCompleteListener { task ->
                        continuation.resume(task)
                    }
            }
            if (result.isSuccessful) {
                val pakar = result.result.toObject<Pakar>()
                if (pakar != null) {
                    emit(Resource.Success(pakar))
                } else {
                    emit(Resource.Error("Error"))
                }
            } else {
                emit(Resource.Error("Error"))
            }
        }
    }

    suspend fun getUserData(
        id: String
    ): Flow<Resource<UserData>> {
        return flow {
            try {
                val result = suspendCoroutine<Task<DocumentSnapshot>> { continuation ->
                    userRef.document(id).get()
                        .addOnCompleteListener { task ->
                            continuation.resume(task)
                        }
                }
                if (result.isSuccessful) {
                    val data =
                        result.result.toObject<UserData>()
                    if (data != null) {
                        emit(Resource.Success(data))
                        return@flow
                    }
                }
                emit(Resource.Error("Gagal mengambil data"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun getPagingPakar(
        exp: Expertise? = null
    ): Flow<PagingData<Pakar>> {
        var query: Query
        if (exp != null) {
            query = pakarRef.orderBy("name", Query.Direction.ASCENDING)
                .whereArrayContains("expertise", exp.id_expertise)
                .limit(8)
        } else {
            query = pakarRef.orderBy("name", Query.Direction.ASCENDING)
                .limit(8)
        }

        val pager = Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            pagingSourceFactory = {
                PakarPagingStore(query)
            }
        )
        return pager.flow
    }

    suspend fun getListExpertise(): Flow<Resource<List<Expertise>>> {
        return flow {
            emit(Resource.Loading())
            val query = expertiseRef.orderBy("name", Query.Direction.ASCENDING)

            try {
                val result = suspendCoroutine<Task<QuerySnapshot>> { continuation ->
                    query.get().addOnCompleteListener { task ->
                        continuation.resume(task)
                    }
                }
                if (result.isSuccessful) {
                    val list =
                        result.result?.documents?.mapNotNull { it.toObject<Expertise>() }
                            ?: emptyList()
                    emit(Resource.Success(list))
                } else {
                    val errorMessage = result.exception?.message ?: "Error"
                    emit(Resource.Error(errorMessage))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    suspend fun getExpertise(expertis: List<String>): Flow<Resource<List<Expertise>>> {
        return flow {
            val list = mutableListOf<Expertise>()
            try {
                emit(Resource.Loading())
                val result = suspendCoroutine<Task<QuerySnapshot>> { continuation ->
                    expertiseRef.whereIn("id_expertise", expertis).get()
                        .addOnCompleteListener { task ->
                            continuation.resume(task)
                        }
                }
                if (result.isSuccessful) {
                    for (i in result.result) {
                        val x = i.toObject<Expertise>()
                        list.add(x)
                    }
                    emit(Resource.Success(list))
                } else {
                    emit(Resource.Error("Error"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            }
        }
    }

    fun getPagingMateri(): Flow<PagingData<Materi>> {
        val query: Query = materiRef.orderBy("title", Query.Direction.ASCENDING)
            .limit(8)
        val pager = Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            pagingSourceFactory = {
                MateriPagingSource(query)
            }
        )
        return pager.flow
    }

    suspend fun getDetailMateri(data: String): Flow<Resource<Materi>> {
        return flow {
            emit(Resource.Loading())
            val result = suspendCoroutine<Task<DocumentSnapshot>> { continuation ->
                materiRef.document(data).get()
                    .addOnCompleteListener { task ->
                        continuation.resume(task)
                    }
            }
            if (result.isSuccessful) {
                val pakar = result.result.toObject<Materi>()
                if (pakar != null) {
                    emit(Resource.Success(pakar))
                } else {
                    emit(Resource.Error("Error"))
                }
            } else {
                emit(Resource.Error("Error"))
            }
        }
    }

    suspend fun getGallery(): Flow<Resource<List<String>>> = flow {
        val downloadUrls = mutableListOf<String>()
        val errors = mutableListOf<Throwable>()
        emit(Resource.Loading())
        try {
            val task = galeryRef.listAll()
            val listResult = task.await() // Use await to suspend until results are available

            for (item in listResult.items) {
                val urlTask = item.downloadUrl
                val url = urlTask.await() // Await download URL for each item
                downloadUrls.add(url.toString())
            }

            emit(Resource.Success(downloadUrls)) // Emit success with the list
        } catch (e: Exception) {
            errors.add(e)
            emit(Resource.Error(errors.firstOrNull().toString())) // Emit error if any occurred
        }
    }

    fun getPaginatedGalleryUrls(): Flow<PagingData<String>> {
        val pagingSource = GalleryPagingSource(galeryRef)
        return Pager(
            config = PagingConfig(pageSize = 8), // Set the same page size as in PagingSource
            pagingSourceFactory = { pagingSource }
        ).flow
    }

//    fun getPaginatedChats(): Flow<PagingData<Chat>> {
//        val pagingSource = ChatsPagingStore(chatRef)
//        return Pager(
//            config = PagingConfig(pageSize = 8), // Set the same page size as in PagingSource
//            pagingSourceFactory = { pagingSource }
//        ).flow
//    }

    fun getChats(id: String, role: String): MutableLiveData<List<Chat>?> {
        val chats = MutableLiveData<List<Chat>?>()
        listenerChats = chatRef.orderByChild("members/$role").equalTo(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        try {
                            val chatList = mutableListOf<Chat>()
                            for (chatSnapshot in snapshot.children) {
                                val chat = chatSnapshot.getValue(Chat::class.java)
                                chat?.let {
                                    chatList.add(it)
                                }
                            }
//                            chatList.sortWith(compareByDescending<Chat> { it.lastChatStatus == "sent" }
//                                .thenByDescending { it.lastTimestamp })
                            chatList.sortByDescending { it.lastTimestamp }


                            chats.value = chatList
                        } catch (e: Exception) {
                            chats.value = null
                        }
                    } else {
                        chats.value = listOf()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    chats.value = null
                }

            })

        return chats
    }

    suspend fun getChatId(idPakar: String, idTenant: String): Chat? {
        val list = mutableListOf<Chat>()
        val x = chatRef.orderByChild("members/tenant").equalTo(idTenant).get().await()
        for (data in x.children) {
            data.getValue(Chat::class.java)?.let {
                if (it.members?.pakar == idPakar) {
                    list.add(it)
                }
            }
        }
        if (list.isNullOrEmpty()) {
            val key = chatRef.push().key
            try {
                val x = Chat(
                    id_chat = key!!,
                    numberChatDone = 0,
                    members = Members(pakar = idPakar, tenant = idTenant)
                )
                chatRef.child(key!!).setValue(x).await()
                return x
            } catch (e: Exception) {
                return null
            }
        } else {
            return list[0]
        }
    }

//    suspend fun getChatById(chatID: String): Chat? {
//        val x = chatRef.child(chatID).get().await()
//        val y = x.getValue(Chat::class.java)
//        return y
//    }

    fun readMessage(chatId: String) {
        val updates = HashMap<String, Any>()
        updates["lastChatStatus"] = "read"
        chatRef.child(chatId).updateChildren(updates)
    }

    fun getChatMessages(idChat: String): MutableLiveData<List<ChatMessage>?> {
        val chats = MutableLiveData<List<ChatMessage>?>()
        listenerChatMessages = chatMessageRef.child(idChat)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        try {
                            val chatList = mutableListOf<ChatMessage>()
                            for (chatSnapshot in snapshot.children) {
                                val chat = chatSnapshot.getValue(ChatMessage::class.java)
                                chat?.let {
                                    chatList.add(it)
                                }
                            }

                            chats.value = chatList
                        } catch (e: Exception) {
                            chats.value = null
                        }
                    } else {
                        chats.value = listOf()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    chats.value = null
                }

            })

        return chats
    }

    suspend fun getUserIDbyRoleId(roleID: String): String? {
        try {
            val result = userRef.whereEqualTo("id_role", roleID).get().await()
            val userList = result.toObjects(UserData::class.java)
            if (userList.isNotEmpty()) {
                return userList[0].id_user
            } else {
                return null
            }
        } catch (e: Exception) {
            return null
        }
    }

    fun getChatData(chatId: String): MutableLiveData<Chat?> {
        val chats = MutableLiveData<Chat?>()
        listenerChatData = chatRef.child(chatId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        try {
                            val chat = snapshot.getValue(Chat::class.java)

                            chats.value = chat
                        } catch (e: Exception) {
                            chats.value = null
                        }
                    } else {
                        chats.value = null
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    chats.value = null
                }

            })

        return chats
    }


    suspend fun sendChat(
        chatID: String,
        data: ChatMessage,
        updates : HashMap<String, Any>?
    ): MutableLiveData<Resource<String>> {
        val key = chatMessageRef.push().key
        val chat = MutableLiveData<Resource<String>>()
        data.id_messages = key
        chatMessageRef.child(chatID).child(key!!).setValue(data).addOnCompleteListener {
            if (it.isSuccessful) {
                if (updates!=null) {
                    chatRef.child(chatID).updateChildren(updates).addOnCompleteListener {
                        if (it.isSuccessful) {
                            chat.value = Resource.Success("Berhasil mengirim data")
                        } else {
                            chat.value = Resource.Error("gagal mengirim data")
                        }
                    }
                }
            } else {
                chat.value = Resource.Error("gagal mengirim data")
            }
        }.await()
        return chat
    }

    suspend fun getForm():String?{
        val updates = HashMap<String, Any>()
        updates["chatStatus"] = "done"
        val form =formRef.get().await()
        return form.getValue(String::class.java)
    }

    fun logOut() {
        if (::listenerChats.isInitialized) chatRef.removeEventListener(listenerChats)
        if (::listenerChatData.isInitialized) chatRef.removeEventListener(listenerChatData)
        if (::listenerChatMessages.isInitialized) chatMessageRef.removeEventListener(
            listenerChatMessages
        )

    }
}