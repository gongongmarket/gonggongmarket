package com.hansung.c.gonggongmarket.presenter.main.fragment.chat_room

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gonggongmarket.R
import com.example.gonggongmarket.databinding.FragmentChatRoomBinding
import com.google.android.material.snackbar.Snackbar
import com.hansung.c.gonggongmarket.presenter.general.PagingLoadStateAdapter
import com.hansung.c.gonggongmarket.presenter.general.ViewBindingFragment
import com.hansung.c.gonggongmarket.presenter.general.addDividerDecoration
import com.hansung.c.gonggongmarket.presenter.general.registerObserverForScrollToTop
import com.hansung.c.gonggongmarket.presenter.general.setListeners
import com.hansung.c.gonggongmarket.presenter.main.fragment.chat_room.chatting.ChattingActivity
import kotlinx.coroutines.launch

class ChatRoomFragment : ViewBindingFragment<FragmentChatRoomBinding>() {

    private val viewModel: ChatRoomViewModel by activityViewModels()
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChatRoomBinding
        get() = FragmentChatRoomBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.bind()
        val adapter = ChatRoomAdapter(::onClickChatItem)
        initRecyclerView(adapter)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                adapter.refresh()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    updateUi(it, adapter)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.bind()
    }

    private fun initRecyclerView(adapter: ChatRoomAdapter) = with(binding) {
        recyclerView.adapter =
            adapter.withLoadStateFooter(PagingLoadStateAdapter { adapter.retry() })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addDividerDecoration()
        loadState.emptyText.text = getString(R.string.is_not_chatting_room)
        loadState.setListeners(adapter, swipeRefreshLayout)
        adapter.registerObserverForScrollToTop(recyclerView)
    }

    private fun updateUi(uiState: ChatRoomUiState, adapter: ChatRoomAdapter) {
        adapter.submitData(viewLifecycleOwner.lifecycle, uiState.chatRooms)

        if (uiState.errorMessage != null) {
            viewModel.errorMessageShown()
            showSnackBar(uiState.errorMessage)
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun onClickChatItem(chatRoomItemUiState: ChatRoomItemUiState) {
        val intent = ChattingActivity.getIntent(
            requireContext(), chatRoomItemUiState.uuid
        )
        launcher.launch(intent)
    }
}