package com.diklat.tanyapakar.ui.tanyapakar

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.model.Pakar
import com.diklat.tanyapakar.core.util.EXTRA_ID
import com.diklat.tanyapakar.core.util.Helper
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ActivityDetailPakarBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailPakarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPakarBinding
    private val viewModel: ListPakarViewModel by viewModels()
    private lateinit var adapter:AdapterExpertise
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPakarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        val layoutManagerCommonTopic = FlexboxLayoutManager(this)
        layoutManagerCommonTopic.flexDirection = FlexDirection.ROW
        binding.rvSpesifikasi.layoutManager = layoutManagerCommonTopic
        adapter = AdapterExpertise(false) {}
        val id = intent.getStringExtra(EXTRA_ID)
        id?.let {
            lifecycleScope.launch {
                viewModel.getDetailPakar(it).observe(this@DetailPakarActivity){
                    when(it){
                        is Resource.Loading->{
                            showLoading(true)
                        }
                        is Resource.Success->{
                            showLoading(false)
                            it?.data?.let {
                                showData(it)
                            }
                        }
                        is Resource.Error->{
                            Toast.makeText(this@DetailPakarActivity,"Error", Toast.LENGTH_SHORT).show()
                            showLoading(false)
                        }
                    }
                }
            }

        }


    }

    private fun showData(it: Pakar) {
        Glide.with(binding.imgProfile).load(it.profile_img).placeholder(R.drawable.bg_home).into(binding.imgProfile)
        binding.tvName.text = it.name
        if(it.jabatan_struktural!=null) {
            binding.llJabatanStruktural.visibility= View.VISIBLE
            binding.tvJabatanStruktural.text = it.jabatan_struktural
        }else{
            binding.llJabatanStruktural.visibility= View.GONE
        }
        if(it.tmt_struktural!=null) {
            binding.llTmtStruktural.visibility= View.VISIBLE
            binding.tvTmtStruktural.text = it.tmt_struktural
        }else{
            binding.llTmtStruktural.visibility= View.GONE
        }
        if(it.jabatan_fungsional!=null){
            binding.llJabatanFungsional.visibility= View.VISIBLE
            binding.tvJabatanFungsional.text = it.jabatan_fungsional
        }else{
            binding.llJabatanFungsional.visibility= View.GONE
        }
        if(it.tmt_fungsional!=null) {
            binding.llTmtFungsional.visibility= View.VISIBLE
            binding.tvTmtFungsional.text = it.tmt_fungsional
        }else{
            binding.llTmtFungsional.visibility= View.GONE
        }
        binding.tvPendidikan.text = it.last_education
        Log.d("TAG",it.last_education.toString())
        viewModel.expertise.observe(this){all->
            if (!all.isEmpty()){
                binding.rvSpesifikasi.visibility=View.VISIBLE
                binding.tvLabelSpesifikasi.visibility=View.VISIBLE
                it.expertise?.let {it->
                    Log.d("TAG",it.toString())
                    adapter.submitList(Helper.convertExpertiseNamesToListExpertise(it,all))
                }
            }else{
                binding.rvSpesifikasi.visibility=View.GONE
                binding.tvLabelSpesifikasi.visibility=View.GONE
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