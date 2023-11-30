package com.hansung.c.gonggongmarket.model

data class UserDetail(
    val uuid: String,
    val name: String,
    val email: String?,
    val profileImageUrl: String?
) : java.io.Serializable