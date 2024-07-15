package com.diklat.tanyapakar.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.util.CHAT_ID
import com.diklat.tanyapakar.core.util.EXTRA_ID
import com.diklat.tanyapakar.core.util.PAKAR_ID
import com.diklat.tanyapakar.ui.login.AuthViewModel
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ActivityChatMessageBinding
import com.example.tanyapakar.databinding.ActivityChatsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatMessageActivity : AppCompatActivity() {
    private lateinit var binding:ActivityChatMessageBinding
    private lateinit var adapter:ChatMessagesAdapter
    private val viewModel: ChatViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        this.binding.rvChats.layoutManager=layoutManager

        var chatId = intent.getStringExtra(CHAT_ID)
        var pakarID = intent.getStringExtra(PAKAR_ID)

        authViewModel.getToken().observe(this){
            if(!it.isNullOrBlank()){
                adapter=ChatMessagesAdapter(it)
                this.binding.rvChats.adapter=adapter

                Log.d("chaterror2",chatId.toString())
                Log.d("chaterror2",pakarID.toString())
                if(chatId.isNullOrBlank()&&pakarID!=null){
                    lifecycleScope.launch {
                        chatId = viewModel.getChatId(pakarID,it)
                        chatId?.let { it1 -> getChat(it1) }
                    }
                }else{
                    chatId?.let { it1 -> getChat(it1) }
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

    private fun getChat(chatId:String){
        viewModel.getChats(chatId).observe(this@ChatMessageActivity) {
            Log.d("chat",it.toString())
            it?.let {
                if (it.isNotEmpty()) {
                    adapter.list = it.toMutableList()
                    adapter.notifyDataSetChanged()
                    val lastIndex = it.size - 1
                    binding.rvChats.scrollToPosition(lastIndex)
                    binding.etInputChat.isEnabled=true
                } else {
                    binding.etInputChat.isEnabled=true

                }
            }
        }
    }
}