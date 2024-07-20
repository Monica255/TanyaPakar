package com.diklat.tanyapakar.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.diklat.tanyapakar.core.data.Resource
import com.diklat.tanyapakar.core.data.source.firebase.LoginType
import com.diklat.tanyapakar.ui.home.HomeActivity
import com.example.tanyapakar.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding :ActivityLoginBinding
//    var isDataValid = false
    var authInput = ""
    var isEmail=false
    var isNUmber=false

    private lateinit var  mAuth: FirebaseAuth
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()

        val emailStream = RxTextView.textChanges(binding.etMasukNomor)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }

        emailStream.subscribe{isNotValid->
            isEmail=!isNotValid
        }

        val numberStream = RxTextView.textChanges(binding.etMasukNomor)
            .skipInitialValue()
            .map { number ->
                !isPhoneNumberValid(number.toString())
            }

        numberStream.subscribe{isNotValid->
            isNUmber=!isNotValid
        }

        binding.btMasuk.setOnClickListener {
            var authType:LoginType?= if(isNUmber)LoginType.PHONE else if (isEmail) LoginType.EMAIL else null

            if(authType!=null){
                authInput=binding.etMasukNomor.text.toString().trim()
                lifecycleScope.launch {
                    viewModel.login(authInput, authType!!).observe(this@LoginActivity){
                        when(it){
                            is Resource.Loading->{
                                showLoading(true)
                            }
                            is Resource.Success->{
                                showLoading(false)
                                if(it?.data!=null){
                                    Toast.makeText(this@LoginActivity,"Berhasil masuk",Toast.LENGTH_SHORT).show()
                                    viewModel.saveToken(it.data)
                                }else{
                                    viewModel.saveToken("guess")
                                    Toast.makeText(this@LoginActivity,"Email/nomor Anda tidak terdaftar. Anda saat ini masuk sebagai tamu.",Toast.LENGTH_SHORT).show()
                                }
                            }
                            is Resource.Error->{
                                showLoading(false)
                                Toast.makeText(this@LoginActivity,it.message,Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

            }else{
                Toast.makeText(this@LoginActivity,"Data tidak valid. Pastikan Anda memasukkan email atau nomor telepon",Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getToken().observe(this) { it ->
            if (it!=""&&it!=null) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)

            }
        }
    }

    fun isPhoneNumberValid(input: String): Boolean{
        val phoneNumberPattern = Pattern.compile("^08[1-9][0-9]{7,}\$") // Regex pattern for the specific format
        return if (phoneNumberPattern.matcher(input).matches()) {
                true
            } else {
               false
            }
        }

    private fun showLoading(isShowLoading: Boolean) {
        binding.pbLoading.visibility = if (isShowLoading) View.VISIBLE else View.GONE
    }

}