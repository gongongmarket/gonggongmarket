package com.hansung.c.gonggongmarket.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hansung.c.gonggongmarket.model.Chat
import com.hansung.c.gonggongmarket.model.ChatRoom
import com.hansung.c.gonggongmarket.presenter.general.getFormattedRelativeTimeAgo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hansung.c.gonggongmarket.data.dto.ChatDto
import com.hansung.c.gonggongmarket.data.dto.ChatRoomDto
import com.hansung.c.gonggongmarket.data.dto.UserDto
import com.hansung.c.gonggongmarket.data.paging.ChatRoomPagingSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

object ChatRepository {

    const val PAGE_SIZE = 20

    fun getMyChatRoom(): Flow<PagingData<ChatRoom>> {
        try {
            return Pager(PagingConfig(pageSize = PAGE_SIZE)) { ChatRoomPagingSource() }.flow
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun createChattingRoom(
        postWriterUuid: String, postUuid: String
    ): Result<Unit> {
        val currentUser = Firebase.auth.currentUser
        require(currentUser != null)
        val db = Firebase.firestore
        val chatRoomCollection = db.collection("rooms")
        val chatRoomUuid = UUID.randomUUID().toString()

        return try {
            val chatRoomDto = ChatRoomDto(
                uuid = chatRoomUuid,
                postUuid = postUuid,
                chatWriterUserUid = postWriterUuid,
                chatAppliedUserUid = currentUser.uid,
                time = Date()
            )
            chatRoomCollection.document(chatRoomUuid).set(chatRoomDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private val messages = mutableListOf<Chat>()

    suspend fun getAllMessages(roomUuid: String): Flow<List<Chat>> {
        val currentUser = Firebase.auth.currentUser
        require(currentUser != null)

        val db = Firebase.firestore

        return callbackFlow {
            val subscription = db.collection("rooms").document(roomUuid).collection("chat")
                .limit(PAGE_SIZE.toLong()).orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                    if (snapshot != null) {
                        snapshot.documentChanges.forEach { dc ->
                            if (dc.type == DocumentChange.Type.ADDED) {
                                val chatDto = dc.document.toObject(ChatDto::class.java)
                                val chat = Chat(
                                    uuid = chatDto.uuid,
                                    isMain = currentUser.uid == chatDto.userUuid,
                                    message = chatDto.message,
                                    date = chatDto.date.getFormattedRelativeTimeAgo(),
                                    profileImage = null
                                )
                                if (!messages.any { it.uuid == chat.uuid }) {
                                    messages.add(chat)
                                }
                            }
                        }
                        trySend(messages.toList())
                    } else {
                        trySend(emptyList())
                    }
                }
            awaitClose {
                subscription.remove()
            }
        }
    }


    suspend fun sendMessage(roomUuid: String, message: String): Result<Unit> {
        val currentUser = Firebase.auth.currentUser
        require(currentUser != null)
        val db = Firebase.firestore
        val chatRoomCollection = db.collection("rooms")
        val uuid = UUID.randomUUID().toString()

        return try {
            val chatDto = ChatDto(
                uuid = uuid, message = message, date = Date(), userUuid = currentUser.uid
            )
            chatRoomCollection.document(roomUuid)
            chatRoomCollection.document(roomUuid).collection("chat").add(chatDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChattingDetail(roomUuid: String): Result<ChatRoom> {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser
        val userCollection = db.collection("users")
        val saleCollection = db.collection("rooms")
        check(currentUser != null)

        return try {
            val chatRoomDto =
                saleCollection.document(roomUuid).get().await().toObject(ChatRoomDto::class.java)!!

            val userUuid = if (chatRoomDto.chatAppliedUserUid != currentUser.uid) {
                chatRoomDto.chatAppliedUserUid
            } else {
                chatRoomDto.chatWriterUserUid
            }

            val otherUser =
                userCollection.document(userUuid).get().await().toObject(UserDto::class.java)
            Result.success(
                ChatRoom(
                    uuid = chatRoomDto.uuid,
                    chatAppliedUserName = otherUser!!.name,
                    chatAppliedUserProfileImage = otherUser.profileImageUrl
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}