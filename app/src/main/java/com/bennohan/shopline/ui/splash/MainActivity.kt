package com.bennohan.shopline.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.shopline.R
import com.bennohan.shopline.data.Session
import com.bennohan.shopline.data.room.UserDao
import com.bennohan.shopline.databinding.ActivityMainBinding
import com.bennohan.shopline.ui.home.HomeActivity
import com.bennohan.shopline.ui.login.LoginActivity
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : NoViewModelActivity<ActivityMainBinding>(R.layout.activity_main) {

    @Inject
    lateinit var session: Session

    @Inject
    lateinit var userDao: UserDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this, R.color.backround)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.backround)

        observe()

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    // val for checking login
                    val isUser = userDao.isLogin()
                    //Handler Looper Splash
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (isUser) {
                            //isUser = True
                            openActivity<HomeActivity>()
                            finish()
                        } else {
                            //isUser = False
                            openActivity<LoginActivity>()
                            finish()
                        }
                    }, 3000)
                }
            }
        }
    }

}