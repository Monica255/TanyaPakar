package com.diklat.tanyapakar.ui.materi

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.model.Log
import com.diklat.tanyapakar.core.data.source.model.Materi
import com.diklat.tanyapakar.core.util.EXTRA_ID
import com.diklat.tanyapakar.core.util.ViewEvents
import com.diklat.tanyapakar.core.util.ViewEventsMateri
import com.diklat.tanyapakar.ui.log.AddLogActivity
import com.diklat.tanyapakar.ui.login.AuthViewModel
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ActivityMateriBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MateriActivity : AppCompatActivity() {
    private val viewModel: MateriViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    lateinit var adapter:MateriAdapter
    private lateinit var binding: ActivityMateriBinding

    private val onCLick: ((Materi) -> Unit) = { data ->
        val intent = Intent(this, DetailMateriActivity::class.java)
        intent.putExtra(EXTRA_ID, data.id_materi)
        startActivity(intent)
    }

    private val onDelete: ((Materi) -> Unit) = { data ->
        showConfirmDialogDelete(data)
    }

    private val launcherIntent= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.getData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMateriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        val layoutManagerForumPost = LinearLayoutManager(this)
        binding.rvMateri.layoutManager = layoutManagerForumPost

        binding.fabAdd.setOnClickListener {
            launcherIntent.launch(Intent(this, AddMateriActivity::class.java))
        }
        authViewModel.getToken().observe(this){
            if(it!=null){
                lifecycleScope.launch {
                    authViewModel.getUserData(it).observe(this@MateriActivity){it->
                        when(it){
                            is Resource.Loading->{}
                            is Resource.Success->{
                                it?.data?.let {
                                    binding.fabAdd.visibility = if(it.role=="pakar") View.VISIBLE else View.GONE
                                }
                            }
                            is Resource.Error->{}
                        }
                    }
                }
                adapter = MateriAdapter(onCLick,onDelete,it)
                binding.rvMateri.adapter = adapter
                viewModel.getData()
                viewModel.pagingData.observe(this) { it ->
                    it.observe(this) {
                        if (it != null) {
                            adapter.submitData(lifecycle, it)
                        }
                    }
                }

                lifecycleScope.launch {
                    adapter.loadStateFlow.collectLatest { loadStates ->
                        showLoading(loadStates.refresh is LoadState.Loading)
                    }
                }
            }
        }
    }

    private fun showConfirmDialogDelete(data: Materi) {
        val builder = AlertDialog.Builder(this)
        val mConfirmDialog = builder.create()
        builder.setTitle(getString(R.string.hapus))
        builder.setMessage(getString(R.string.yakin_ingin_hapus_materi))
        builder.create()

        builder.setPositiveButton(getString(R.string.ya)) { _, _ ->
            delete(data)
            showLoading(false)
        }

        builder.setNegativeButton(getString(R.string.tidak)) { _, _ ->
            mConfirmDialog.cancel()
        }
        builder.show()
    }

    private fun delete(data: Materi){
        lifecycleScope.launch {
            viewModel.deleteMateri(data).observe(this@MateriActivity){
                when(it){
                    is Resource.Loading->{
                        android.util.Log.d("loadinggg","isloading")
                        showLoading(true)}
                    is Resource.Success->{
                        android.util.Log.d("loadinggg","loading finish")
                        showLoading(false)
                        it?.data?.let {
                            viewModel.onViewEvent(ViewEventsMateri.Remove(data))
                        }
                    }
                    is Resource.Error->{
                        showLoading(false)
                    }
                }
            }
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
}