package com.hansung.c.gonggongmarket.presenter.sign_up

sealed class UserInfoUiState {
    object None : UserInfoUiState()
    object Loading : UserInfoUiState()
    object SuccessToSave : UserInfoUiState()
    data class FailedToSave(val exception: Throwable) : UserInfoUiState()
}
