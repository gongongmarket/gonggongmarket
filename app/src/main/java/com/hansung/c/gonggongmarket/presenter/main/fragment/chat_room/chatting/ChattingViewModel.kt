package com.hansung.c.gonggongmarket.presenter.main.fragment.chat_room.chatting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hansung.c.gonggongmarket.data.repo.ChatRepository
import com.hansung.c.gonggongmarket.presenter.main.fragment.chatroom.toUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChattingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChattingUiState())
    val uiState = _uiState.asStateFlow()

    private var messages = mutableListOf<ChatItemUiState>()

    fun updateMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    fun bind(roomUuid: String) {
        _uiState.update { it.copy(isLoading = true) }
        getCurrentRoomDetail(roomUuid)
        getCurrentRoomMessages(roomUuid)
        _uiState.update { it.copy(isLoading = false) }
    }

    private fun getCurrentRoomDetail(roomUuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            check(true)
            val result = ChatRepository.getChattingDetail(roomUuid)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        currentChatItemUiState = result.getOrNull()!!.toUiState()
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = result.exceptionOrNull()!!.localizedMessage
                    )
                }
            }
        }
    }

    private fun getCurrentRoomMessages(roomUuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            check(true)
            val messagesFlow = ChatRepository.getAllMessages(roomUuid)
            messagesFlow.collect { lists ->
                if (lists.isNotEmpty()) {
                    if (messages.isEmpty()) {
                        messages = lists.map { chat ->
                            chat.toUiState()
                        }.toMutableList()
                        _uiState.update { ui ->
                            ui.copy(chats = messages)
                        }
                    } else {
                        messages += lists.map { chat ->
                            chat.toUiState()
                        }.toMutableList()
                        _uiState.update { ui ->
                            ui.copy(chats = messages)
                        }
                    }
                    _uiState.update { ui ->
                        ui.copy(chats = lists.map { chat ->
                            chat.toUiState()
                        }.toMutableList())
                    }
                }
            }
        }
    }

    fun sendMessage() {
        val message = uiState.value.message
        val roomUuid = uiState.value.currentChatItemUiState!!.uuid
        viewModelScope.launch(Dispatchers.IO) {
            check(true)
            val result = ChatRepository.sendMessage(roomUuid = roomUuid, message = message)
            if (result.isFailure) {
                _uiState.update { it.copy(errorMessage = result.exceptionOrNull()!!.localizedMessage) }
            }
        }
    }
}