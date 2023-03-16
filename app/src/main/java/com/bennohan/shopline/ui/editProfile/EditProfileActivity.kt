package com.bennohan.shopline.ui.editProfile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.shopline.R
import com.bennohan.shopline.base.BaseActivity
import com.bennohan.shopline.data.Session
import com.bennohan.shopline.databinding.ActivityEditProfileBinding
import com.bennohan.shopline.ui.profile.ProfileActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileActivity :  BaseActivity<ActivityEditProfileBinding, EditProfileViewModel>(R.layout.activity_edit_profile) {

    @Inject
    lateinit var session: Session
    private var username: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = resources.getColor(R.color.white)

        getUser()
        observe()

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnConfirm.setOnClickListener {
            validateForm()
        }


    }

    private fun getUser() {
        val user = session.getUser()
        binding.user = user

    }

    private fun validateForm() {
        val name = binding.tvName.textOf()
        val phone = binding.etPhone.textOf()

        if (name.isEmpty()) {
            tos("Nama tidak boleh kosong")
            return
        }

        if (phone.isEmpty()) {
            tos("Nomor Telphone tidak boleh kosong")
            return
        }


            if (name == username) {
                tos("tidak ada data yang berubah")
                return
            }
            viewModel.updateUser(name, phone)

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Update Profile")
                            ApiStatus.SUCCESS -> {
                                tos(it.message ?: "Berhasil Update Profile")
                                loadingDialog.dismiss()
                                finish()
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                            }
                            ApiStatus.EXPIRED -> {

                            }
                            else -> loadingDialog.setResponse("Else")
                        }
                    }
                }
            }
        }
    }

}