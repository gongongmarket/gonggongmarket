package com.hansung.c.gonggongmarket.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hansung.c.gonggongmarket.data.repo.ChatRepository.PAGE_SIZE
import com.hansung.c.gonggongmarket.data.dto.ChatRoomDto
import com.hansung.c.gonggongmarket.data.dto.UserDto
import com.hansung.c.gonggongmarket.model.ChatRoom
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ChatRoomPagingSource : PagingSource<QuerySnapshot, ChatRoom>() {

    private val currentUserId = Firebase.auth.currentUser!!.uid
    private val userCollection = Firebase.firestore.collection("users")

    private val chatRoomQueries =
        Firebase.firestore.collection("rooms").orderBy("time", Query.Direction.DESCENDING)
            .limit(PAGE_SIZE.toLong())

    override fun getRefreshKey(state: PagingState<QuerySnapshot, ChatRoom>): QuerySnapshot? {

        return null
    }

    override suspend fun load(
        params: LoadParams<QuerySnapshot>
    ): LoadResult<QuerySnapshot, ChatRoom> {

        return try {
            val currentPage = params.key ?: chatRoomQueries.get().await()
            if (currentPage.isEmpty) {
                return LoadResult.Page(
                    data = emptyList(), prevKey = null, nextKey = null
                )
            }

            val lastVisiblePost = currentPage.documents[currentPage.size() - 1]
            val nextPage = chatRoomQueries.startAfter(lastVisiblePost).get().await()
            val chatRoomDtos = currentPage.toObjects(ChatRoomDto::class.java)
            val chatRooms = chatRoomDtos.filter { chatRoomDto ->
                chatRoomDto.chatWriterUserUid == currentUserId || chatRoomDto.chatAppliedUserUid == currentUserId
            }.map { chatRoomDto ->
                val userUuid = if (chatRoomDto.chatAppliedUserUid != currentUserId) {
                    chatRoomDto.chatAppliedUserUid
                } else {
                    chatRoomDto.chatWriterUserUid
                }
                val writer =
                    userCollection.document(userUuid).get().await().toObject(UserDto::class.java)
                ChatRoom(
                    uuid = chatRoomDto.uuid,
                    chatAppliedUserName = writer!!.name,
                    chatAppliedUserProfileImage = writer.profileImageUrl
                )
            }
            LoadResult.Page(
                data = chatRooms, prevKey = null, nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}