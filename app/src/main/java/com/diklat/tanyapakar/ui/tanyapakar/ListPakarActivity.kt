package com.diklat.tanyapakar.ui.tanyapakar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.model.Expertise
import com.diklat.tanyapakar.core.data.source.model.Pakar
import com.diklat.tanyapakar.core.util.EXTRA_ID
import com.diklat.tanyapakar.core.util.PAKAR_ID
import com.diklat.tanyapakar.core.util.USER_PAKAR_ID
import com.diklat.tanyapakar.ui.chat.ChatMessageActivity
import com.diklat.tanyapakar.ui.chat.ChatsActivity
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ActivityListPakarBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
interface OnGetData {
    fun handleData(data: Expertise?)
}
@AndroidEntryPoint
class ListPakarActivity : AppCompatActivity(),OnGetData {
    private lateinit var binding: ActivityListPakarBinding
    private lateinit var adapterForum: PagingPakarAdapter
    private val viewModel:ListPakarViewModel by viewModels ()

    private val onCLick: ((Pakar) -> Unit) = { data ->
        val intent = Intent(this, DetailPakarActivity::class.java)
        intent.putExtra(EXTRA_ID, data.id_pakar)
        startActivity(intent)
    }

    private val onCLickChat: ((String,String) -> Unit) = { idPakar,userIdPakar ->
        val intent = Intent(this, ChatMessageActivity::class.java)
        intent.putExtra(PAKAR_ID, idPakar)
        intent.putExtra(USER_PAKAR_ID, userIdPakar)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListPakarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        val layoutManagerForumPost = GridLayoutManager(this,2)
        binding.rvPakar.layoutManager = layoutManagerForumPost



        viewModel.pagingData.observe(this) { it ->
            it.observe(this) {
                if (it != null) {
                    adapterForum.submitData(lifecycle, it)
//                    binding.rvForumPost.smoothScrollToPosition(1)
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }


        lifecycleScope.launch {
            viewModel.getListExpertise().observe(this@ListPakarActivity) {
                when (it) {
                    is Resource.Loading -> {
                        showLoading(true)
                    }
                    is Resource.Success -> {
                        showLoading(false)
                        if(it.data==null||it.data.isEmpty()){
                            Toast.makeText(this@ListPakarActivity, "Gagal mendapatkan topik", Toast.LENGTH_SHORT).show()
                        }

                        it.data?.toMutableList()?.let { it1 ->
                            viewModel.expertise.value = it1
                            adapterForum = PagingPakarAdapter(it1,onCLick,onCLickChat,viewModel,this@ListPakarActivity,lifecycleScope)
                            binding.rvPakar.adapter = adapterForum
                            viewModel.getData()
                        }

                        lifecycleScope.launch {
                            adapterForum.loadStateFlow.collectLatest { loadStates ->
                                showLoading(loadStates.refresh is LoadState.Loading)
                            }
                        }

                    }
                    is Resource.Error -> {
                        showLoading(false)
                        Toast.makeText(this@ListPakarActivity, "Gagal mendapatkan topik", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getData()
        }

        binding.btnExpertise.setOnClickListener {
            val topicFragment= ExpertiseFragment()
            topicFragment.show(supportFragmentManager,"expertise_dialog")
        }
        binding.fabChat.setOnClickListener {
            startActivity(Intent(this, ChatsActivity::class.java))
        }

    }

    private fun showLoading(isShowLoading: Boolean) {
        binding.pbLoading.visibility = if (isShowLoading) View.VISIBLE else View.GONE
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

    override fun handleData(data: Expertise?) {
        binding.btnExpertise.text= data?.name ?: getString(R.string.semua)
        viewModel.getData(data)
        viewModel.mExp=data

    }
}