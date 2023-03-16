package com.bennohan.shopline.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bennohan.shopline.R
import com.bennohan.shopline.data.Session
import com.bennohan.shopline.databinding.ActivityMainBinding
import com.bennohan.shopline.ui.home.HomeActivity
import com.bennohan.shopline.ui.login.LoginActivity
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : NoViewModelActivity<ActivityMainBinding>(R.layout.activity_main) {

    @Inject
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = resources.getColor(R.color.splashColor)
        window.navigationBarColor = resources.getColor(R.color.splashColor)


        //Handler Looper Splash
        Handler(Looper.getMainLooper()).postDelayed({
            val isUser = session.getUser()
            if (isUser == null){
                openActivity<LoginActivity>()
                finish()
            }else{
                openActivity<HomeActivity>()
                finish()

            }
        },3000)

    }
}