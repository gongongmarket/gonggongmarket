package com.hansung.c.gonggongmarket.presenter.main.fragment.sale

import androidx.paging.PagingData
import com.hansung.c.gonggongmarket.model.Sale
import com.hansung.c.gonggongmarket.model.SortType
import com.hansung.c.gonggongmarket.presenter.general.getFormattedCost

data class SaleUiState(
    val salePosts: PagingData<SaleItemUiState> = PagingData.empty(),
    val errorMessage: String? = null,
    val sortType: SortType = SortType.LATEST_ORDER,
    val flags: Boolean = false
)

data class SaleItemUiState(
    val uuid: String,
    val title: String,
    val writerUuid: String,
    val writerName: String,
    val writerProfileImageUrl: String?,
    val content: String,
    val imageUrl: String,
    val isMine: Boolean,
    val time: String,
    val cost: String,
    val possibleSale: Boolean
) : java.io.Serializable

fun Sale.toUiState() = SaleItemUiState(
    uuid = uuid,
    title = title,
    writerUuid = writerUuid,
    writerName = writerName,
    writerProfileImageUrl = writerProfileImageUrl,
    content = content,
    imageUrl = imageUrl,
    isMine = isMine,
    cost = cost.getFormattedCost(),
    time = time,
    possibleSale = possibleSale
)
