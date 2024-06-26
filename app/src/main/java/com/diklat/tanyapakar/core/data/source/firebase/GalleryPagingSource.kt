import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class GalleryPagingSource(private val storageRef: StorageReference) : PagingSource<String, String>() {

    private val pageSize = 6 // Set your desired page size here

    override suspend fun load(params: LoadParams<String>): LoadResult<String, String> {
        val currentPage = params.key
        val nextPageToken: String?

        try {
            Log.d("galeryyy","paging param"+ params.key.toString())
            val listResult = if (currentPage != null) {
                storageRef.list(pageSize, currentPage.toString()).await()
            } else {
                storageRef.list(pageSize).await()
            }

            Log.d("galeryyy","paging"+ "downloadUrls.toString()")
            nextPageToken = listResult.pageToken
            val downloadUrls = listResult?.items?.let { extractDownloadUrls(it) }
            Log.d("galeryyy","paging"+ downloadUrls.toString())
            return LoadResult.Page(
                data = downloadUrls!!,
                prevKey = null, // Always null for initial load
                nextKey = nextPageToken
            )
        } catch (e: Exception) {
            Log.d("galeryyy","paging"+ e.message.toString())
            return LoadResult.Error(e) // Handle errors centrally
        }
    }

    override fun getRefreshKey(state: PagingState<String, String>): String? {
        // Implement your data refresh strategy here (optional)
        return null
    }

    private suspend fun extractDownloadUrls(items: List<StorageReference>): List<String> {
        val downloadUrls = mutableListOf<String>()
        for (item in items) {
            val urlTask = item.downloadUrl
            try {
                val url = urlTask.await()
                downloadUrls.add(url.toString())
            } catch (e: Exception) {
                // Handle download URL retrieval errors for each item (optional)
            }
        }
        return downloadUrls
    }

    private fun getNextPageToken(currentPage:Int,listResult: ListResult): Int? {
        // Check if there are more items after limiting retrieved items
        return if (listResult.items.size == pageSize) {
            currentPage + 1
        } else {
            null
        }
    }
}



