package com.diklat.tanyapakar.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.model.UserData
import com.diklat.tanyapakar.ui.login.AuthViewModel
import com.diklat.tanyapakar.ui.login.LoginActivity
import com.diklat.tanyapakar.ui.tanyapakar.ListPakarActivity
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
            } else {
                lifecycleScope.launch {
                    viewModel.getUserData(token).observe(this@HomeActivity) {
                        when (it) {
                            is Resource.Loading -> {
                                showLoading(true)
                            }
                            is Resource.Success -> {
                                showLoading(false)
                                it?.data?.let {
                                    setData(it)
                                }
                            }

                            is Resource.Error -> {
                                showLoading(true)
                            }
                        }
                    }
                }
            }
        }

        binding.btLogout.setOnClickListener {
            showConfirmDialog()
        }

        binding.cvTanyaPakar.setOnClickListener {
            startActivity(Intent(this, ListPakarActivity::class.java))
        }

    }

    private fun setData(data: UserData) {
        if (data.role == "pakar") {
            binding.cvTanyaPakar.visibility = View.GONE
            binding.fabChat.visibility = View.VISIBLE
        } else if (data.role == "tenant") {
            binding.cvTanyaPakar.visibility = View.VISIBLE
            binding.fabChat.visibility = View.GONE
        } else {
            binding.cvTanyaPakar.visibility = View.GONE
            binding.fabChat.visibility = View.GONE
        }
        binding.tvName.text = data.name
        binding.tvPhone.text = data.phone ?: "-"
        binding.tvRole.text = (data.role ?: "pengunjung").capitalize()
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