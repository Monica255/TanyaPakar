package com.diklat.tanyapakar.ui.materi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.model.Log
import com.diklat.tanyapakar.core.data.source.model.Materi
import com.diklat.tanyapakar.ui.log.LogViewModel
import com.diklat.tanyapakar.ui.login.AuthViewModel
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ActivityAddMateriBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddMateriActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddMateriBinding
    private var filePath: Uri? = null
    private var fileName: String? = null
    private val viewModel: MateriViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var id_user:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMateriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        authViewModel.getToken().observe(this){token->
            id_user=token
            binding.btUpload.setOnClickListener {
                val title = binding.etTitle.text.toString().trim()
                val desc = binding.etDescription.text.toString().trim()
                if(filePath!=null&&fileName!=null&&desc.isNotBlank()&&title.isNotBlank()){
                    if(id_user!=null){
                        val data = Materi(
                            null,
                            title,
                            filePath!!.toString(),
                            fileName,
                            desc,
                            id_user,
                            System.currentTimeMillis()
                        )
                        lifecycleScope.launch {
                            viewModel.uploadMateri(data,filePath!!).observe(this@AddMateriActivity){
                                when(it){
                                    is Resource.Loading->{showLoading(true)}
                                    is Resource.Success->{
                                        showLoading(false)
                                        it?.data?.let {
                                            setResult(RESULT_OK)
                                            Toast.makeText(this@AddMateriActivity,it, Toast.LENGTH_SHORT).show()
                                            finish()
                                        }
                                    }
                                    is Resource.Error->{
                                        showLoading(false)
                                        it?.message?.let {
                                            Toast.makeText(this@AddMateriActivity,it, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        Toast.makeText(this,"Gagal mengunggah", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this,"Mohon lengkapi data dengan benar", Toast.LENGTH_SHORT).show()
                }
            }
            binding.cvUpload.setOnClickListener {
                startGallery()
            }
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // Accept all types initially
        intent.putExtra(
            Intent.EXTRA_MIME_TYPES,
            arrayOf("application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        ) // Specify MIME types for PDF and DOCX
        val chooser = Intent.createChooser(intent, getString(R.string.choose_file))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            filePath = result.data?.data as Uri
            filePath?.let {
                fileName = getFileNameFromUri(it)
                binding.tvName.text= fileName
            }
            binding.tvName.visibility =if(filePath!=null) View.VISIBLE else View.GONE
            binding.llUpload.visibility =if(filePath!=null) View.GONE else View.VISIBLE
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = it.getString(displayNameIndex)
                }
            }
        }
        return fileName
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