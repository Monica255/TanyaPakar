package com.diklat.tanyapakar.core.data.source.firebase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.diklat.tanyapakar.core.data.source.model.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await


class LogPagingSource (
    private val query: Query
) : PagingSource<QuerySnapshot, Log>() {
    var list= mutableListOf<Log>()
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Log>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Log> {
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
            list=currentPage.toObjects(Log::class.java)
            LoadResult.Page(
                data = list,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            android.util.Log.d("TAG","error "+e.message.toString())
            LoadResult.Error(e)
        }
    }

}