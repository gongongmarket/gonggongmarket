package com.hansung.c.gonggongmarket.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hansung.c.gonggongmarket.model.Sale
import com.hansung.c.gonggongmarket.model.SortType
import com.hansung.c.gonggongmarket.presenter.general.getFormattedRelativeTimeAgo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hansung.c.gonggongmarket.data.dto.SaleDto
import com.hansung.c.gonggongmarket.data.dto.UserDto
import com.hansung.c.gonggongmarket.data.repo.SaleRepository
import kotlinx.coroutines.tasks.await

class SalePagingSource(
    private val getWriterUuids: suspend () -> List<String>,
    private val sortType: SortType,
    private val flags: Boolean
) : PagingSource<QuerySnapshot, Sale>() {

    private val currentUserId = Firebase.auth.currentUser!!.uid
    private val userCollection = Firebase.firestore.collection("users")

    private val tempPostsQueries =
        Firebase.firestore.collection("posts").limit(SaleRepository.PAGE_SIZE.toLong())

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Sale>): QuerySnapshot? {
        return null
    }

    override suspend fun load(
        params: LoadParams<QuerySnapshot>
    ): LoadResult<QuerySnapshot, Sale> {
        val writerUuids = getWriterUuids()
        var postQueries = tempPostsQueries

        when (sortType.sortNumber) {
            0 -> postQueries = tempPostsQueries.orderBy("time", Query.Direction.DESCENDING)
            1 -> postQueries = tempPostsQueries.orderBy("cost", Query.Direction.DESCENDING)
            2 -> postQueries = tempPostsQueries.orderBy("cost", Query.Direction.ASCENDING)
        }

        return try {
            val currentPage = params.key ?: postQueries.get().await()
            if (currentPage.isEmpty) {
                return LoadResult.Page(
                    data = emptyList(), prevKey = null, nextKey = null
                )
            }
            val lastVisiblePost = currentPage.documents[currentPage.size() - 1]
            val nextPage = postQueries.startAfter(lastVisiblePost).get().await()
            var saleDtos = currentPage.toObjects(SaleDto::class.java)
            if (flags) {
                saleDtos = saleDtos.filter { saleDto ->
                    saleDto.possibleSale
                }
            }
            val sales = saleDtos.filter { saleDto ->
                writerUuids.contains(saleDto.writerUuid)
            }.map { saleDto ->
                val writer = userCollection.document(saleDto.writerUuid).get().await()
                    .toObject(UserDto::class.java)
                Sale(
                    uuid = saleDto.uuid,
                    title = saleDto.title,
                    writerUuid = writer!!.uuid,
                    writerName = writer.name,
                    writerProfileImageUrl = writer.profileImageUrl,
                    content = saleDto.content,
                    imageUrl = saleDto.imageUrl,
                    isMine = saleDto.writerUuid == currentUserId,
                    cost = saleDto.cost,
                    time = saleDto.time.getFormattedRelativeTimeAgo(),
                    possibleSale = saleDto.possibleSale
                )
            }

            LoadResult.Page(
                data = sales, prevKey = null, nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
