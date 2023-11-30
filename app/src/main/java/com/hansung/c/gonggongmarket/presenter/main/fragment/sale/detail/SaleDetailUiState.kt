package com.hansung.c.gonggongmarket.presenter.main.fragment.sale.detail

import com.hansung.c.gonggongmarket.presenter.main.fragment.sale.SaleItemUiState

data class SaleDetailUiState(
    val selectedSaleItem: SaleItemUiState? = null,
    val currentUserUuid: String,
    val currentSelectedSaleItemPossible: Boolean? = null,
    val errorMessage: String? = null,
    val isDeleteSuccess: Boolean = false,
    val isLoading: Boolean = false
)