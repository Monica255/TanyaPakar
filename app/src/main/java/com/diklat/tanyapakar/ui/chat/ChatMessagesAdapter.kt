package com.diklat.tanyapakar.ui.chat

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.diklat.tanyapakar.core.data.source.model.ChatMessage
import com.example.tanyapakar.databinding.ItemBubbleChatBinding


class ChatMessagesAdapter(private val uid:String): RecyclerView.Adapter<ChatMessagesAdapter.ItemViewHolder>() {

    var list= mutableListOf<ChatMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessagesAdapter.ItemViewHolder {
        val binding =
            ItemBubbleChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ChatMessagesAdapter.ItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount()=list.size

    inner class ItemViewHolder(private val binding: ItemBubbleChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(chat: ChatMessage) {
            binding.itemChat.setData(chat, uid)
        }
    }

}