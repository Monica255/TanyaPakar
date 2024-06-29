package com.diklat.tanyapakar.ui.galery

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.ui.materi.MateriAdapter
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ActivityGaleryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GaleryActivity : AppCompatActivity() {
    lateinit var binding:ActivityGaleryBinding
    private val viewModel:GaleryViewModel by viewModels()
    lateinit var adapter : GalleryPagingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGaleryBinding.inflate(layoutInflater)
        setActionBar()
        setContentView(binding.root)

        val layoutManager = GridLayoutManager(this,2)
        binding.rvGalery.layoutManager = layoutManager

        adapter = GalleryPagingAdapter{url->
            val fragment = GalleryFragment.newInstance(url)
            fragment.show(supportFragmentManager,"gallery_fragment")
        }
        binding.rvGalery.adapter = adapter

//        lifecycleScope.launch {
//            viewModel.getGalery().observe(this@GaleryActivity){
//                when(it){
//                    is Resource.Loading->{
//                        showLoading(true)
//                    }
//                    is Resource.Success->{
//                        showLoading(false)
//                        it.data?.let {
////                            adapter.list=it
////                            adapter.notifyDataSetChanged()
//                            Log.d("galeryyy",it.toString())
//                        }
//                    }
//                    is Resource.Error->{
//                        showLoading(false)
//                        Log.d("galeryyy",it.message.toString())
//                        Toast.makeText(this@GaleryActivity,it.message.toString(),Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }


        viewModel.getPaginatedGalleryUrls().observe(this@GaleryActivity){
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                showLoading(loadStates.refresh is LoadState.Loading)
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