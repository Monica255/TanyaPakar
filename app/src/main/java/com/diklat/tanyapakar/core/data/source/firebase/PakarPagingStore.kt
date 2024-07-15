package com.diklat.tanyapakar.core.data.source.firebase

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.diklat.tanyapakar.core.data.source.model.Pakar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class PakarPagingStore (
    private val query: Query
) : PagingSource<QuerySnapshot, Pakar>() {
    var list= mutableListOf<Pakar>()
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Pakar>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Pakar> {
        return try {
            val currentPage = params.key ?: query.get().await()
            var lastVisibleProduct: DocumentSnapshot
            var nextPage: QuerySnapshot?
            if(currentPage.size()!=0){
                lastVisibleProduct = currentPage.documents[currentPage.size() - 1]
                nextPage = query.startAfter(lastVisibleProduct).get().await()
            }else{
                nextPage=null
            }

            //list= mutableListOf()
            list=currentPage.toObjects(Pakar::class.java)
            LoadResult.Page(
                data = list,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            Log.d("TAG","error "+e.message.toString())
            LoadResult.Error(e)
        }
    }

}