package com.diklat.tanyapakar.ui.chat

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.model.Chat
import com.diklat.tanyapakar.ui.login.AuthViewModel
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ItemChatBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class ChatAdapter(
    private val onClick: ((Chat) -> Unit),
    private val viewModell: AuthViewModel,
    private val viewModell2: ChatViewModel,
    private val coroutineScope: CoroutineScope,
    private val owner: LifecycleOwner,
    private val uid: String
) :
    RecyclerView.Adapter<ChatAdapter.ItemViewHolder>() {

    var list = mutableListOf<Chat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ItemViewHolder {
        val binding =
            ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ChatAdapter.ItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    inner class ItemViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: Chat) {
            if (data.lastChatStatus == "read") {
                binding.imgRead.visibility = View.GONE
            } else {
                if (data.lastChat == uid) {
                    binding.imgRead.visibility = View.GONE
                } else {
                    binding.imgRead.visibility = View.VISIBLE
                }
            }
            binding.tvMessage.text = data.lastMessage
            coroutineScope.launch {
                val x = if (data.members?.pakar == uid) data.members.tenant else data.members?.pakar
                viewModell.getUserData(x!!).observe(owner) {
                    when (it) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            it?.data?.let {
                                binding.tvName.text = it.name
                                Glide.with(itemView).load(it.img_profile).placeholder(R.drawable.bg_home).into(binding.imgProfile)
                            }
                        }

                        is Resource.Error -> {}
                    }
                }
            }
//            Glide.with(itemView).load(data).placeholder(R.drawable.bg_home).into(binding.imgProfile)
            binding.root.setOnClickListener {
                if(data.lastChatStatus=="sent"||data.lastChat!=uid){
                    viewModell2.readMessage(data.id_chat)
                }
                onClick.invoke(data)
            }
        }
    }

}