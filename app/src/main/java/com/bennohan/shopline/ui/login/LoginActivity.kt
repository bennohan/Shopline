package com.bennohan.shopline.ui.login

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.shopline.R
import com.bennohan.shopline.base.BaseActivity
import com.bennohan.shopline.databinding.ActivityLoginBinding
import com.bennohan.shopline.ui.home.HomeActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.navigationBarColor = ContextCompat.getColor(this,R.color.mainColor)
        window.statusBarColor = ContextCompat.getColor(this,R.color.mainColor)

        binding.btnLogin.setOnClickListener {
            //Show alert if Text View is Empty
            if (binding.etPhone.isEmptyRequired(R.string.mustFill) ||
                binding.etPassword.isEmptyRequired(R.string.mustFill)
            ) {
                return@setOnClickListener
            }
            login()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("login...in")
                            ApiStatus.SUCCESS -> {
                                binding.root.snacked(it.message ?: "Login Success")
//                                tos(it.message ?: "Login Success")
                                binding.root.snacked("Login Success")
                                loadingDialog.dismiss()
                                openActivity<HomeActivity>()
                                finish()
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                loadingDialog.dismiss()
                                loadingDialog.setResponse(it.message ?: return@collect)
                                binding.root.snacked(it.message ?: "Login Failed")
//                                tos(it.message ?: "Login Failed")
//                                tos(it.message ?: "Please Check Your Phone And Password Again")
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }

    }

    //Function Login
    private fun login() {
        val phone = binding.etPhone.textOf()
        val password = binding.etPassword.textOf()
        viewModel.login(phone, password)
    }

}