package com.diklat.tanyapakar.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.diklat.tanyapakar.ui.home.HomeActivity
import com.diklat.tanyapakar.ui.login.AuthViewModel
import com.diklat.tanyapakar.ui.login.LoginActivity
import com.example.tanyapakar.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getToken().observe(this){
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    val intent: Intent = if (it!=""&&it!=null) {
                        Intent(this, HomeActivity::class.java)
                    } else {
                        Intent(this, LoginActivity::class.java)
                    }
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Log.e("ERROR", e.message.toString())
                }
            }, DELAY_TIME)
        }

    }
    companion object {
        const val DELAY_TIME: Long = 2_000
    }
}