package com.diklat.tanyapakar.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.model.Chat
import com.diklat.tanyapakar.core.data.source.model.ChatMessage
import com.diklat.tanyapakar.core.util.CHAT_ID
import com.diklat.tanyapakar.core.util.CHAT_STATUS
import com.diklat.tanyapakar.core.util.PAKAR_ID
import com.diklat.tanyapakar.core.util.USER_PAKAR_ID
import com.diklat.tanyapakar.ui.login.AuthViewModel
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ActivityChatMessageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatMessageBinding
    private lateinit var adapter: ChatMessagesAdapter
    private val viewModel: ChatViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    //    private var chat: Chat? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        this.binding.rvChats.layoutManager = layoutManager

        var chatId = intent.getStringExtra(CHAT_ID)
        var pakarID = intent.getStringExtra(PAKAR_ID)
        var userPakarID = intent.getStringExtra(USER_PAKAR_ID)
        var chatStatus = intent.getStringExtra(CHAT_STATUS)

        authViewModel.getToken().observe(this) { token ->
            if (!token.isNullOrBlank()) {
                adapter = ChatMessagesAdapter(token)
                this.binding.rvChats.adapter = adapter

                lifecycleScope.launch {
                    if (chatId.isNullOrBlank() && userPakarID != null) {
                        val chat = viewModel.getChatId(userPakarID, token)
                        chatId = chat?.id_chat
                        chatStatus = chat?.chatStatus
                    }
                    if (!chatId.isNullOrBlank() ) {
                        getChat(chatId!!)
                        if(chatStatus != null&&userPakarID!=null){
                            if (chatStatus == "start" || chatStatus == "done") {
                                val time = System.currentTimeMillis()
                                val initialChat = ChatMessage(
                                    null,
                                    userPakarID,
                                    getString(R.string.initial_chat),
                                    time
                                )
                                sendChat(chatId!!, initialChat,true)
                            }
                        }

                        binding.btnSend.setOnClickListener{
                            val msg = binding.etInputChat.text.trim().toString()
                            if(msg.isNotEmpty()){
                                lifecycleScope.launch {
                                    val chat=ChatMessage(null,token,msg,System.currentTimeMillis())
                                    sendChat(chatId!!,chat)
                                    if(token!=userPakarID){
                                        val chat2=ChatMessage(null,userPakarID,"Mohon menunggu ya, pertanyaanmu segera dijawab",System.currentTimeMillis())
                                        sendChat(chatId!!,chat2,true,false)
                                    }
                                }
                            }
                        }

                    }
                }

            }
        }


//        lifecycleScope.launch {
//            val x = if (data.members?.pakar == uid) data.members.tenant else data.members?.pakar
//            Log.d("chaterror", x.toString())
//            viewModell.getUserData(x!!).observe(owner) {
//                when (it) {
//                    is Resource.Loading -> {}
//                    is Resource.Success -> {
//                        it?.data?.let {
//                            binding.tvName.text = it.name
//                            Glide.with(itemView).load(it.img_profile).placeholder(R.drawable.bg_home).into(binding.imgProfile)
//                        }
//                    }
//
//                    is Resource.Error -> {}
//                }
//            }
//        }


        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun getChat(chatId: String) {
        viewModel.getChatMessages(chatId).observe(this@ChatMessageActivity) {
            Log.d("chat", it.toString())
            it?.let {
                if (it.isNotEmpty()) {
                    adapter.list = it.toMutableList()
                    adapter.notifyDataSetChanged()
                    val lastIndex = it.size - 1
                    binding.rvChats.scrollToPosition(lastIndex)
                    binding.etInputChat.isEnabled = true
                } else {
                    binding.etInputChat.isEnabled = true

                }
            }
        }
    }

    private fun sendChat(chatID: String, data: ChatMessage,bot:Boolean=false,updateStatus:Boolean=true) {
        lifecycleScope.launch {
            viewModel.sendChat(chatID, data,bot,updateStatus).observe(this@ChatMessageActivity) {
                binding.etInputChat.setText("")
                when (it) {
                    is Resource.Success -> {
                        binding.btnSend.isClickable=true
                    }

                    is Resource.Error -> {
                        binding.btnSend.isClickable=true
                    }

                    is Resource.Loading -> {
                        binding.btnSend.isClickable=false
                    }
                }
            }
        }
    }
}