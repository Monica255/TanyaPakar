package com.diklat.tanyapakar.core.data.source.firebase

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.diklat.tanyapakar.core.data.source.model.Chat
import com.diklat.tanyapakar.core.data.source.model.Pakar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.database.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class ChatsPagingStore (
    private val ref: DatabaseReference
) : PagingSource<Query, Chat>() {
    val uid= FirebaseAuth.getInstance().currentUser?.uid
    override fun getRefreshKey(state: PagingState<Query, Chat>): Query? {
        return null
    }

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, Chat> {
        return try {
            val currentPage = params.key
            val listResult = if(currentPage!=null){
                Log.d("chaterror","a")
                ref.startAfter(currentPage.toString()).get().await()
            }else{
                Log.d("chaterror","b")
                ref.get()
                    .await()
            }
            val y= listResult.children.map { it.getValue(Chat::class.java)!! }
            Log.d("chaterror",listResult.children.toString())
            Log.d("chaterror",y.size.toString())
            val nextPage = ref.startAfter(y.last().id_chat)

            LoadResult.Page(
                data = y,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            Log.d("TAG","error "+e.message.toString())
            LoadResult.Error(e)
        }
    }

}