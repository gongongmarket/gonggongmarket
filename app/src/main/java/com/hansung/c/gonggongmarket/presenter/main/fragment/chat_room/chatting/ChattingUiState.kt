package com.hansung.c.gonggongmarket.presenter.main.fragment.chat_room.chatting

import com.hansung.c.gonggongmarket.model.Chat
import com.hansung.c.gonggongmarket.presenter.main.fragment.chat_room.ChatRoomItemUiState

data class ChattingUiState(
    val currentChatItemUiState: ChatRoomItemUiState? = null,
    val chats: MutableList<ChatItemUiState>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val message: String = ""
)

data class ChatItemUiState(
    val uuid: String,
    val isMain: Boolean,
    val message: String,
    val date: String,
    val profileImage: String?
)

fun Chat.toUiState() = ChatItemUiState(
    uuid = uuid,
    isMain = isMain,
    message = message,
    date = date,
    profileImage = profileImage
)