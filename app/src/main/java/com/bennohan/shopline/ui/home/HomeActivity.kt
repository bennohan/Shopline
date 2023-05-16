package com.bennohan.shopline.ui.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.shopline.R
import com.bennohan.shopline.base.BaseActivity
import com.bennohan.shopline.data.Cons
import com.bennohan.shopline.data.Product
import com.bennohan.shopline.data.room.UserDao
import com.bennohan.shopline.databinding.ActivityHomeBinding
import com.bennohan.shopline.databinding.ItemHomeBinding
import com.bennohan.shopline.ui.cart.CartActivity
import com.bennohan.shopline.ui.detailProduct.DetailProductActivity
import com.bennohan.shopline.ui.profile.ProfileActivity
import com.bennohan.shopline.ui.search.SearchActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

//    @Inject
//    lateinit var session: Session

    @Inject
    lateinit var userDao: UserDao


    private val adapter by lazy {
        ReactiveListAdapter<ItemHomeBinding, Product>(R.layout.item_home).initItem { position, data ->
            openActivity<DetailProductActivity> {
                putExtra(Cons.PRODUCT.ID, data)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        supportActionBar?.hide()

        observe()
        getUser()
        imageSlider()

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)


        //Ask About This
        binding.rvHome.adapter = adapter
        binding.rvHome2.adapter = adapter
        binding.rvHome3.adapter = adapter

        viewModel.getProduct()

        binding.ivProfile.setOnClickListener {
            openActivity<ProfileActivity>()
        }

        binding.btnCart.setOnClickListener {
            openActivity<CartActivity>()
        }

        binding.editText.setOnClickListener {
            openActivity<SearchActivity>()
        }
        binding.tvShowMore.setOnClickListener {
            binding.cardShowMore.visibility = View.VISIBLE
        }
        binding.tvHideProduct.setOnClickListener {
            binding.cardShowMore.visibility = View.GONE
        }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.SUCCESS -> {
                                getUserData()
                            }
                            else -> {

                            }
                        }
                    }


                }

                launch {
                    viewModel.product.collect { product ->
                        adapter.submitList(product)
                    }
                }

            }
        }
    }

    private fun imageSlider() {
        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel("https://marketplace.canva.com/EAE7S1WxJW8/1/0/1600w/canva-orange-modern-promotion-sport-shoes-banner-suttfK_s9zg.jpg"))
        imageList.add(
            SlideModel(
                "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/supper-sale-banner-ad-for-shoes-offre-design-template-263b3813e52a6a6eb85fa45fd49ca3b4_screen.jpg?ts=1625994393",
                ScaleTypes.FIT
            )
        )
        imageList.add(
            SlideModel(
                "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/shoes-sale-bannuer-design-template-38d8c87b5b44afb4906d2d55743a98ae_screen.jpg?ts=1616352652",
                ScaleTypes.FIT
            )
        )

        binding.imageView6.setImageList(imageList)
    }


    private fun getUser() {
        viewModel.getProfile()
    }

    private fun getUserData() {
        userDao.getUser().observe(this@HomeActivity) { userData ->
            binding.user = userData
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this@HomeActivity)
        builder.setTitle("Exit")
        builder.setMessage("Are you sure you want to exit?.")
            .setPositiveButton("Exit") { dialog, id ->
                this@HomeActivity.finish()
            }
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.dismiss()
            }
        val dialog: AlertDialog = builder.create()

        // Set the color of the positive button text
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.red))
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.black))
        }
        dialog.show()
    }

}