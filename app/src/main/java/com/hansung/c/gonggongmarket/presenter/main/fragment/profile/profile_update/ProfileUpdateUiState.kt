package com.hansung.c.gonggongmarket.presenter.main.fragment.profile.profile_update

import android.graphics.Bitmap

data class ProfileUpdateUiState(
    val name: String = "",
    val selectedImageBitmap: Bitmap? = null,
    val isImageChanged: Boolean = false,
    val successToSave: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)