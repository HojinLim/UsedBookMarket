package com.example.usedbookmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.usedbookmarket.adapter.ChatAdapter.ChatItemRowViewHolder
import com.example.usedbookmarket.databinding.ItemChatRowBinding
import com.example.usedbookmarket.model.Chat

class ChatAdapter(val clickListener: (Chat) -> Unit): ListAdapter<Chat, ChatItemRowViewHolder>(diffUtil) {
    inner class ChatItemRowViewHolder(private val binding: ItemChatRowBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chatModel: Chat){

            /*
            Glide
                .with(binding.itemChatListProfile.context)
                .load(chatModel.userProfileUrl)
                .into(binding.itemChatListProfile)

            Glide
                .with(binding.itemChatListBookImg.context)
                .load(chatModel.bookImgUrl)
                .into(binding.itemChatListBookImg)

            binding.itemChatListNickName.text= chatListModel.userNickName
            binding.itemChatListTime.text= chatListModel.time
            binding.itemChatListLastMsg.text= chatListModel.lastMsg

            binding.root.setOnClickListener {
                clickListener(chatListModel)
            }

             */
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemRowViewHolder {
        return ChatItemRowViewHolder(
            ItemChatRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatItemRowViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil= object : DiffUtil.ItemCallback<Chat>() {
            override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem.uid == newItem.uid
            }
        }
    }
}