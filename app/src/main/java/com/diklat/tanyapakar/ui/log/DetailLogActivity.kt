package com.diklat.tanyapakar.ui.log

import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.util.EXTRA_ID
import com.diklat.tanyapakar.ui.materi.MateriViewModel
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ActivityDetailLogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

@AndroidEntryPoint
class DetailLogActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDetailLogBinding
    private val viewModel: LogViewModel by viewModels()
    var isLoading= MutableLiveData<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()
        isLoading.value=true
        val webSettings = binding.webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.domStorageEnabled=true
        webSettings.builtInZoomControls=true
        webSettings.displayZoomControls=true
        webViewClient()

        val id =  intent.getStringExtra(EXTRA_ID)
        if(id!=null){
            lifecycleScope.launch {
                viewModel.getDetailLog(id).observe(this@DetailLogActivity){
                    when(it){
                        is Resource.Loading->{
                            showLoading(true)
                        }
                        is Resource.Success->{

                            it.data?.let {
                                it.file?.let { it1 ->
                                    try {
                                        val url = URLEncoder.encode(it1, "UTF-8")
                                        binding.webview.loadUrl("https://docs.google.com/gview?embedded=true&url=$url").also {
//                                            binding.webview.loadUrl("https://docs.google.com/gview?embedded=true&url=$url")
                                        }
                                        binding.swipeRefresh.setOnRefreshListener {
                                            binding.webview.loadUrl("https://docs.google.com/gview?embedded=true&url=$url")
                                        }
                                    } catch (e: UnsupportedEncodingException) {
                                        e.printStackTrace()
                                    }
                                }
                                binding.toolbarTitle.text= it.file_name
                            }
                        }
                        is Resource.Error->{
                            showLoading(false)
                            Toast.makeText(this@DetailLogActivity, "Gagal memuat", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }else{
            Toast.makeText(this, "Gagal memuat", Toast.LENGTH_SHORT).show()
        }

        isLoading.observe(this){
            showLoading(it)
        }
    }

    private fun webViewClient(){
        binding.webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                isLoading.value=false
//                Log.d("webbbb",binding.webview.isShown.toString())
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(this@DetailLogActivity, "Berhasil memuat materi", Toast.LENGTH_LONG).show()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                view?.loadUrl(url)
                return true
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError) {
                val builder = AlertDialog.Builder(this@DetailLogActivity)
                var message = "SSL Certificate error."
                Log.d("materiiii","sslerror")
                when (error.primaryError) {
                    SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                    SslError.SSL_EXPIRED -> message = "The certificate has expired."
                    SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                    SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
                }

                message += " Do you want to continue anyway?"

                builder.setTitle("SSL Certificate Error")
                builder.setMessage(message)

                builder.setPositiveButton("continue") { dialog, which ->
                    handler.proceed()
                }

                builder.setNegativeButton("cancel") { dialog, which ->
                    handler.cancel()
                }

                val alertDialog = builder.create()
                alertDialog.show()
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