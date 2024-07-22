package com.diklat.tanyapakar.ui.log

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.diklat.tanyapakar.core.data.source.model.Log
import com.diklat.tanyapakar.core.data.source.model.Materi
import com.diklat.tanyapakar.core.util.EXTRA_ID
import com.diklat.tanyapakar.ui.galery.GaleryActivity
import com.diklat.tanyapakar.ui.login.AuthViewModel
import com.diklat.tanyapakar.ui.materi.DetailMateriActivity
import com.diklat.tanyapakar.ui.materi.MateriAdapter
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ActivityListLogBinding
import com.example.tanyapakar.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@AndroidEntryPoint
class ListLogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListLogBinding
    private lateinit var adapter: LogAdapter
    private val viewModel: LogViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val onCLick: ((Log) -> Unit) = { data ->
        val intent = Intent(this, DetailLogActivity::class.java)
        intent.putExtra(EXTRA_ID, data.id_log)
        startActivity(intent)
    }

    private val launcherIntent= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            getData()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        val layoutManagerForumPost = LinearLayoutManager(this)
        binding.rvLog.layoutManager = layoutManagerForumPost

        adapter = LogAdapter(onCLick, viewModel, this@ListLogActivity)
        binding.rvLog.adapter = adapter

        getData()
        viewModel.pagingData.observe(this) { it ->
            it.observe(this) {
                if (it != null) {
                    adapter.submitData(lifecycle, it)
//                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }

        binding.fabAdd.setOnClickListener {
            launcherIntent.launch(Intent(this, AddLogActivity::class.java))
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                showLoading(loadStates.refresh is LoadState.Loading)
            }
        }
    }

    //
    private fun getData() {
        authViewModel.getToken().observe(this) {
            if (!it.isNullOrBlank()) {
                viewModel.getData(it)
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

    private fun showLoading(isShowLoading: Boolean) {
        binding.pbLoading.visibility = if (isShowLoading) View.VISIBLE else View.GONE
    }
}