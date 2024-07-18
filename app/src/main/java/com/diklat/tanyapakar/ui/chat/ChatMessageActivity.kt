package com.diklat.tanyapakar.ui.chat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.model.Chat
import com.diklat.tanyapakar.core.data.source.model.ChatMessage
import com.diklat.tanyapakar.core.util.CHAT_ID
import com.diklat.tanyapakar.core.util.CHAT_STATUS
import com.diklat.tanyapakar.core.util.NAME
import com.diklat.tanyapakar.core.util.PAKAR_ID
import com.diklat.tanyapakar.core.util.PHOTO
import com.diklat.tanyapakar.core.util.USER_PAKAR_ID
import com.diklat.tanyapakar.ui.login.AuthViewModel
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ActivityChatMessageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatMessageBinding
    private lateinit var adapter: ChatMessagesAdapter
    private val viewModel: ChatViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var chatData:Chat?=null
    //    private var chat: Chat? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        this.binding.rvChats.layoutManager = layoutManager

        var chatId = intent.getStringExtra(CHAT_ID)
        var userPakarID = intent.getStringExtra(USER_PAKAR_ID)
        var chatStatus = intent.getStringExtra(CHAT_STATUS)

        var name = intent.getStringExtra(NAME)
        var photo = intent.getStringExtra(PHOTO)
        binding.tvName.text = name?:"Unknown"
        Glide.with(this).load(photo).placeholder(R.drawable.bg_home).into(binding.imgPhoto)
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
                        getChat(chatId!!,token)
                        if(userPakarID!=null){
                            if(token!=userPakarID){
                                if (chatStatus == null || chatStatus == "done") {
                                    val time = System.currentTimeMillis()
                                    val initialChat = ChatMessage(
                                        null,
                                        userPakarID,
                                        getString(R.string.initial_chat),
                                        time
                                    )
                                    val updates = HashMap<String, Any>()
                                    updates["lastChatStatus"] = "read"
                                    updates["lastChat"] = initialChat.sentBy!!
                                    updates["lastMessage"] = initialChat.message
                                    updates["lastTimestamp"] = initialChat.timestamp.toLong()
                                    updates["chatStatus"] = "start"
                                    sendChat(chatId!!, initialChat,updates)
                                }
                            }
                        }

                        binding.btnSend.setOnClickListener{
                            val msg = binding.etInputChat.text.trim().toString()
                            if(msg.isNotEmpty()){
                                lifecycleScope.launch {
                                    val chat=ChatMessage(null,token,msg,System.currentTimeMillis())
                                    val updates = HashMap<String, Any>()
                                    updates["lastChatStatus"] = "sent"
                                    updates["lastChat"] = chat.sentBy!!
                                    updates["lastMessage"] = chat.message
                                    updates["lastTimestamp"] = chat.timestamp.toLong()
                                    updates["chatStatus"] = "ongoing"
                                    sendChat(chatId!!,chat,updates)
                                    if(token!=userPakarID){
                                        delay(1000)
                                        val chat2=ChatMessage(null,userPakarID,"Mohon menunggu ya, pertanyaanmu segera dijawab",System.currentTimeMillis())
                                        sendChat(chatId!!,chat2,null)
                                    }
                                }
                            }
                        }

                        binding.btnEndChat.setOnClickListener {
                            showConfirmDialog(chatId,userPakarID)
                        }

                        viewModel.getChatData(chatId!!).observe(this@ChatMessageActivity){
                            chatData=it
                            if(token!=userPakarID){
                                binding.btnEndChat.visibility=if(chatData?.chatStatus=="ongoing") View.VISIBLE else View.GONE
                            }
                        }
                    }
                }

            }
        }




        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun getChat(chatId: String,token:String) {
        viewModel.getChatMessages(chatId).observe(this@ChatMessageActivity) {
            Log.d("readchat", it?.last().toString())
            it?.let {
                if (it.isNotEmpty()) {
                    adapter.list = it.toMutableList()
                    adapter.notifyDataSetChanged()
                    val lastIndex = it.size - 1
                    binding.rvChats.scrollToPosition(lastIndex)
                    binding.etInputChat.isEnabled = true
                    if(chatData?.lastChat!=token)viewModel.readMessage(chatId)
                } else {
                    binding.etInputChat.isEnabled = true

                }
            }
        }
    }

    private fun sendChat(chatID: String, data: ChatMessage,updates:HashMap<String,Any>?) {
        lifecycleScope.launch {
            viewModel.sendChat(chatID, data,updates).observe(this@ChatMessageActivity) {
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

    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun showConfirmDialog(chatId: String?,userPakarID:String?) {
        val builder = AlertDialog.Builder(this)
        val mConfirmDialog = builder.create()
        builder.setTitle(getString(R.string.keluar))
        builder.setMessage(getString(R.string.yakin_ingin_menghakhiri))
        builder.create()

        builder.setPositiveButton(getString(R.string.ya)) { _, _ ->
            lifecycleScope.launch {
                val form = viewModel.getForm()
                val chat2=ChatMessage(null,userPakarID,"Terima kasih sudah berkonsultasi ^^, silahkan lanjutkan dengan mengisi form evuluasi ya di bawah ini ya.\n\n${form?:""}",System.currentTimeMillis())
                val updates = HashMap<String, Any>()
                updates["lastChatStatus"] = "read"
                updates["lastChat"] = chat2.sentBy!!
                updates["lastMessage"] = chat2.message
                updates["lastTimestamp"] = chat2.timestamp.toLong()
                updates["chatStatus"] = "done"

                val currentNumberChatDone = try {
                    chatData?.numberChatDone?.toInt() ?: 0
                } catch (e: NumberFormatException) {
                    0
                }
                updates["numberChatDone"] = currentNumberChatDone + 1
                sendChat(chatId!!,chat2,updates)
                form?.let { it1 -> openLink(it1) }
            }
        }

        builder.setNegativeButton(getString(R.string.tidak)) { _, _ ->
            mConfirmDialog.cancel()
        }
        builder.show()
    }
}