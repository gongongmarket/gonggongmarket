package com.hansung.c.gonggongmarket.data.dto

import java.util.Date

data class ChatDto(
    val uuid: String = "",
    val userUuid: String = "",
    val message: String = "",
    val date: Date = Date(),
)