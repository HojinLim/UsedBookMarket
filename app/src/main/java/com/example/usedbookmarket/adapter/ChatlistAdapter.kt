package com.example.usedbookmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.usedbookmarket.adapter.ChatlistAdapter.ChatItemViewHolder
import com.example.usedbookmarket.databinding.ItemChatListBinding
import com.example.usedbookmarket.model.ChatList

class ChatlistAdapter(val clickListener: (ChatList) -> Unit): ListAdapter<ChatList, ChatItemViewHolder>(diffUtil) {
    inner class ChatItemViewHolder(private val binding: ItemChatListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chatListModel: ChatList){

            Glide
                .with(binding.itemChatListProfile.context)
                .load(chatListModel.userProfileUrl)
                .into(binding.itemChatListProfile)

            Glide
                .with(binding.itemChatListBookImg.context)
                .load(chatListModel.bookImgUrl)
                .into(binding.itemChatListBookImg)

            binding.itemChatListNickName.text= chatListModel.userNickName
            binding.itemChatListTime.text= chatListModel.time
            binding.itemChatListLastMsg.text= chatListModel.lastMsg

            binding.root.setOnClickListener {
                clickListener(chatListModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        return ChatItemViewHolder(
            ItemChatListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil= object : DiffUtil.ItemCallback<ChatList>() {
            override fun areItemsTheSame(oldItem: ChatList, newItem: ChatList): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: ChatList, newItem: ChatList): Boolean {
                return oldItem.userProfileUrl == newItem.userProfileUrl
            }
        }
    }
}