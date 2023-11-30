package com.hansung.c.gonggongmarket.presenter.main.fragment.chat_room

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gonggongmarket.R
import com.example.gonggongmarket.databinding.ItemChattingRoomBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ChatRoomViewHolder(
    private val binding: ItemChattingRoomBinding,
    private val onClickChatItem: (ChatRoomItemUiState) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val storageReference = Firebase.storage.reference

    fun bind(uiState: ChatRoomItemUiState) = with(binding) {

        val glide = Glide.with(root)

        val writerReference =
            uiState.chatAppliedUserProfileImage?.let { storageReference.child(it) }

        if (writerReference != null) {
            writerReference.downloadUrl.addOnSuccessListener { uri ->
                glide.load(uri).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fallback(R.drawable.baseline_person_24).circleCrop().into(userProfileImage)
            }
        } else {
            glide.load(R.drawable.baseline_person_24).circleCrop().into(userProfileImage)
        }

        userName.text = uiState.chatAppliedUserName

        root.setOnClickListener { onClickChatItem(uiState) }
    }
}