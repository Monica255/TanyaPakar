package com.diklat.tanyapakar.ui.tanyapakar

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ActivityListPakarBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListPakarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListPakarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListPakarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
//        binding.toolbarTitle.text = title
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}