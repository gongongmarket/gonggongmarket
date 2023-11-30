package com.hansung.c.gonggongmarket.presenter.main.fragment.chat_room

import androidx.paging.PagingData
import com.hansung.c.gonggongmarket.model.ChatRoom

data class ChatRoomUiState(
    val chatRooms: PagingData<ChatRoomItemUiState> = PagingData.empty(),
    val currentUserUuid: String,
    val errorMessage: String? = null
)

data class ChatRoomItemUiState(
    val uuid: String,
    val chatAppliedUserName: String,
    val chatAppliedUserProfileImage: String?
)

fun ChatRoom.toUiState() = ChatRoomItemUiState(
    uuid = uuid,
    chatAppliedUserName = chatAppliedUserName,
    chatAppliedUserProfileImage = chatAppliedUserProfileImage
)