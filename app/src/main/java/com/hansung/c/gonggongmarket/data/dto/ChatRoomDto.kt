package com.hansung.c.gonggongmarket.data.dto

import java.util.Date

data class ChatRoomDto(
    val uuid: String = "",
    val postUuid: String = "",
    val chatWriterUserUid: String = "",
    val chatAppliedUserUid: String = "",
    val time: Date = Date()
)