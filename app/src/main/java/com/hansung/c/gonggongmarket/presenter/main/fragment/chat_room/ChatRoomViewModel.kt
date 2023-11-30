package com.hansung.c.gonggongmarket.presenter.main.fragment.chat_room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.hansung.c.gonggongmarket.data.repo.AuthRepository
import com.hansung.c.gonggongmarket.data.repo.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatRoomViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        ChatRoomUiState(
            currentUserUuid = requireNotNull(
                AuthRepository.currentUserUuid
            )
        )
    )
    val uiState = _uiState.asStateFlow()

    fun bind() {
        viewModelScope.launch(Dispatchers.IO) {
            val pagingFlow = ChatRepository.getMyChatRoom()
            pagingFlow.cachedIn(viewModelScope).collect { pagingData ->
                _uiState.update { state ->
                    state.copy(chatRooms = pagingData.map { it.toUiState() })
                }
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}