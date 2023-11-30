package com.hansung.c.gonggongmarket.presenter.main.fragment.profile

import androidx.paging.PagingData
import com.hansung.c.gonggongmarket.model.UserDetail
import com.hansung.c.gonggongmarket.presenter.main.fragment.sale.SaleItemUiState

data class ProfileUiState(
    val salePosts: PagingData<SaleItemUiState> = PagingData.empty(),
    val userDetail: UserDetail? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
