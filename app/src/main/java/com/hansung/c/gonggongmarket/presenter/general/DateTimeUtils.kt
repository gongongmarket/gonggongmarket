package com.hansung.c.gonggongmarket.presenter.general

import android.text.format.DateUtils
import java.text.ParseException
import java.util.Date

fun Date.getFormattedRelativeTimeAgo(): String {
    try {
        val now = System.currentTimeMillis()
        val ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)
        return ago.toString()
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    throw IllegalStateException()
}