package com.diklat.tanyapakar.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.diklat.tanyapakar.core.data.Resource

import com.diklat.tanyapakar.ui.login.AuthViewModel
import com.diklat.tanyapakar.ui.login.LoginActivity
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getToken().observe(this) { token ->
            if (token == "" || token == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }else{
                // TODO get user data and update UI
                lifecycleScope.launch {
                    viewModel.getUserData(token).observe(this@HomeActivity){
                        when(it){
                            is Resource.Loading->{}
                            is Resource.Success->{
                                it?.data?.let {
                                    binding.tvName.text = it.name
                                }

                            }
                            is Resource.Error->{}
                        }
                    }
                }
            }
        }

        binding.btLogout.setOnClickListener {
            showConfirmDialog()
        }

    }

    private fun showConfirmDialog() {
        val builder = AlertDialog.Builder(this)
        val mConfirmDialog = builder.create()
        builder.setTitle(getString(R.string.keluar))
        builder.setMessage(getString(R.string.yakin_keluar))
        builder.create()

        builder.setPositiveButton(getString(R.string.ya)) { _, _ ->
            viewModel.saveToken("")
            showLoading(false)
        }

        builder.setNegativeButton(getString(R.string.tidak)) { _, _ ->
            mConfirmDialog.cancel()
        }
        builder.show()
    }

    private fun showLoading(isShowLoading: Boolean) {
        binding.pbLoading.visibility = if (isShowLoading) View.VISIBLE else View.GONE
    }
}