package com.diklat.tanyapakar.ui.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.model.Chat
import com.diklat.tanyapakar.core.util.CHAT_ID
import com.diklat.tanyapakar.ui.login.AuthViewModel
import com.example.tanyapakar.databinding.ActivityChatsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatsBinding
    private lateinit var adapter :ChatAdapter
    private val viewModel:ChatViewModel by viewModels()
    private val authViewModel:AuthViewModel by viewModels()
    private val onCLick: ((Chat) -> Unit) = { data ->
        val intent = Intent(this, ChatMessageActivity::class.java)
        intent.putExtra(CHAT_ID, data.id_chat)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()


        val layoutManager = LinearLayoutManager(this)
        this.binding.rvChats.layoutManager=layoutManager

        authViewModel.getToken().observe(this){token->
            if(!token.isNullOrBlank()){
                adapter = ChatAdapter(onCLick,authViewModel,viewModel,lifecycleScope,this,token)
                this.binding.rvChats.adapter=adapter
                lifecycleScope.launch {
                    authViewModel.getUserData(token).observe(this@ChatsActivity) {
                        when (it) {
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                it?.data?.let {
                                    viewModel.getChats(token,it.role.toString()).observe(this@ChatsActivity){
                                        it?.let {
                                            adapter.list=it.toMutableList()
                                            adapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                            }

                            is Resource.Error -> {}
                        }
                    }
                }

            }
        }

    }
    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}