package com.bennohan.shopline.ui.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.shopline.R
import com.bennohan.shopline.base.BaseActivity
import com.bennohan.shopline.data.room.UserDao
import com.bennohan.shopline.databinding.ActivityProfileBinding
import com.bennohan.shopline.ui.editProfile.EditProfileActivity
import com.bennohan.shopline.ui.login.LoginActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.tos
import com.crocodic.core.helper.ImagePreviewHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity :
    BaseActivity<ActivityProfileBinding, ProfileViewModel>(R.layout.activity_profile) {

//    @Inject
//    lateinit var session: Session

    @Inject
    lateinit var userDao: UserDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getUser()

        window.statusBarColor = ContextCompat.getColor(this,R.color.white)


        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnEditProfile.setOnClickListener {
            openActivity<EditProfileActivity>()
        }

        binding.BtnLogout.setOnClickListener {
            logout()
        }

        binding.ivProfile.setOnClickListener {
            ImagePreviewHelper(this).show(binding.ivProfile, binding.user?.image)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    getUser()
                }
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Logout...")
                            ApiStatus.SUCCESS -> {
                                binding.root.snacked("Logout Success")
                                loadingDialog.dismiss()
                                loadingDialog.setResponse(it.message ?: return@collect)
                                openActivity<LoginActivity>()
                                finish()
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                loadingDialog.dismiss()
                                loadingDialog.setResponse(it.message ?: return@collect)
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }
    }

    private fun getUser() {
//        val user = userDao.getUser().observe(this@ProfileActivity)
         userDao.getUser().observe(this@ProfileActivity){ user->
            binding.user = user
        }
    }

    private fun logout() {
        val builder = AlertDialog.Builder(this@ProfileActivity)
        builder.setTitle("Log Out")
        builder.setMessage("Anda yakin ingin Logout")
            .setCancelable(false)
            .setPositiveButton("Logout") { _, _ ->
                // Delete selected note from database
                tos("logout")
                viewModel.logout()
                openActivity<LoginActivity>()
                finishAffinity()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog: AlertDialog = builder.create()

        // Set the color of the positive button text
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, com.crocodic.core.R.color.text_red))
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.black))
        }

        // Show the dialog
        dialog.show()

    }


}