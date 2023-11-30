package com.hansung.c.gonggongmarket.presenter.general

fun Long.getFormattedCost(): String {
    val str = "%,d".format(this)
    return "${str}원"
}