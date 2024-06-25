package com.diklat.tanyapakar.ui.materi

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.diklat.tanyapakar.core.data.source.model.Materi
import com.diklat.tanyapakar.core.util.EXTRA_ID
import com.example.tanyapakar.databinding.ActivityMateriBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MateriActivity : AppCompatActivity() {
    private val viewModel: MateriViewModel by viewModels()
    lateinit var adapter:MateriAdapter
    private lateinit var binding: ActivityMateriBinding

    private val onCLick: ((Materi) -> Unit) = { data ->
        val intent = Intent(this, DetailMateriActivity::class.java)
        intent.putExtra(EXTRA_ID, data.id_materi)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMateriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        val layoutManagerForumPost = LinearLayoutManager(this)
        binding.rvMateri.layoutManager = layoutManagerForumPost

        adapter = MateriAdapter(onCLick,viewModel,this@MateriActivity)
        binding.rvMateri.adapter = adapter


        viewModel.getPagingMateri().observe(this){
            lifecycleScope.launch {
                adapter.submitData(it)
                adapter.notifyDataSetChanged()
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