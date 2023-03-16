package com.bennohan.shopline.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.shopline.R
import com.bennohan.shopline.base.BaseActivity
import com.bennohan.shopline.data.Session
import com.bennohan.shopline.databinding.ActivityProfileBinding
import com.bennohan.shopline.ui.editProfile.EditProfileActivity
import com.bennohan.shopline.ui.login.LoginActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.tos
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity :
    BaseActivity<ActivityProfileBinding, ProfileViewModel>(R.layout.activity_profile) {

    @Inject
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getUser()

        window.statusBarColor = resources.getColor(R.color.white)


        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnEditProfile.setOnClickListener {
            openActivity<EditProfileActivity>()
        }

        binding.tvLogout.setOnClickListener {
            logout()
        }

    }

    private fun getUser() {
        val user = session.getUser()
        binding.user = user
    }

    private fun logout() {
        binding.tvLogout.setOnClickListener {
            viewModel.logout()
            openActivity<LoginActivity>()
            finishAffinity()
        }
    }

}