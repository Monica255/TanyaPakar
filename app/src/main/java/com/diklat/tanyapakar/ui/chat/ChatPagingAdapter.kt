package com.diklat.tanyapakar.ui.chat

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.model.Chat
import com.diklat.tanyapakar.ui.login.AuthViewModel
import com.example.tanyapakar.databinding.ItemChatBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
//
//class ChatPagingAdapter(
//    private val onClick: ((Chat) -> Unit),
//    private val viewModell: AuthViewModel,
//    private val coroutineScope: CoroutineScope,
//    private val owner: LifecycleOwner
//) : PagingDataAdapter<Chat, ChatPagingAdapter.ViewHolder>(Companion) {
//    inner class ViewHolder(private val binding: ItemChatBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        @RequiresApi(Build.VERSION_CODES.O)
//        fun bind(data: Chat) {
//            Log.d("chaterror","binding")
//            val uid = FirebaseAuth.getInstance().currentUser?.uid?:""
//            if(data.members?.tenant==uid){
//                coroutineScope.launch {
//                    viewModell.getUserData(data.members.pakar!!).observe(owner){
//                        when(it){
//                            is Resource.Loading->{}
//                            is Resource.Success->{
//                                it?.data?.let {
//                                    binding.tvName.text = it.name
////                                    Glide.with(itemView).load(data).placeholder(R.drawable.bg_home).into(binding.imgProfile)
//                                }
//                            }
//                            is Resource.Error->{}
//                        }
//                    }
//                }
//
//            }
//            binding.tvMessage.text = data.lastChat
//
////            Glide.with(itemView).load(data).placeholder(R.drawable.bg_home).into(binding.imgProfile)
//            binding.root.setOnClickListener {
//                onClick.invoke(data)
//            }
//        }
//    }
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val data = getItem(position)
//        if (data != null) {
//            holder.bind(data)
//        }
//    }
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding =
//            ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//    companion object : DiffUtil.ItemCallback<Chat>() {
//        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
//            return oldItem.id_chat== newItem.id_chat
//        }
//        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
//            return oldItem.id_chat == newItem.id_chat
//        }
//        override fun getChangePayload(oldItem: Chat, newItem: Chat): Any? = Any()
//    }
//
//}