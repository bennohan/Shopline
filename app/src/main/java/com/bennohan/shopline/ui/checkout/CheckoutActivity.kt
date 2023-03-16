package com.bennohan.shopline.ui.checkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.shopline.R
import com.bennohan.shopline.base.BaseActivity
import com.bennohan.shopline.databinding.ActivityCheckoutBinding
import com.bennohan.shopline.ui.home.HomeActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckoutActivity :
    BaseActivity<ActivityCheckoutBinding, CheckoutViewModel>(R.layout.activity_checkout) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnCheckout.setOnClickListener {
            //Show alert if Text View is Empty
            if (binding.etAddress.isEmptyRequired(R.string.mustFill) ||
                binding.etKota.isEmptyRequired(R.string.mustFill) ||
                binding.etKecamatan.isEmptyRequired(R.string.mustFill) ||
                binding.etProvinsi.isEmptyRequired(R.string.mustFill)
            ) {
                return@setOnClickListener
            }
            checkout()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("login..in")
                            ApiStatus.SUCCESS -> {
                                tos(it.message ?: "Login Success")
                                loadingDialog.setResponse(it.message ?: return@collect)
                                loadingDialog.dismiss()
                                openActivity<HomeActivity>()
                                finish()
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                loadingDialog.dismiss()
                                loadingDialog.setResponse(it.message ?: return@collect)
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

    private fun checkout() {
        val address = binding.etAddress.textOf()
        val provinsi = binding.etProvinsi.textOf()
        val kota = binding.etKota.textOf()
        val kecamatan = binding.etKecamatan.textOf()
        val notes = binding.etNotes.textOf()

        viewModel.checkout(address, provinsi, kota, kecamatan, notes)
    }

}