package com.bennohan.shopline.ui.detailProduct

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.shopline.R
import com.bennohan.shopline.base.BaseActivity
import com.bennohan.shopline.data.Cons
import com.bennohan.shopline.data.ImageSlide
import com.bennohan.shopline.data.Product
import com.bennohan.shopline.databinding.ActivityDetailProductBinding
import com.bennohan.shopline.databinding.ItemDetailBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailProductActivity :
    BaseActivity<ActivityDetailProductBinding, DetailProductViewModel>(R.layout.activity_detail_product) {

    private var product: Product? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageList = ArrayList<SlideModel>()
        productData()

        observe()


        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        //Variable Image slider
        val imageSlider = findViewById<ImageSlider>(R.id.imageSlider)
        imageSlider.setImageList(imageList)

    }


    private fun productData() {
        //Receiving TourData
        val data = intent.getParcelableExtra<Product>(Cons.PRODUCT.ID)
        data?.id?.let { viewModel.getProduct(it) }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.SUCCESS -> {
                                val data = product

                                binding.data = data

                            }
                            else -> {

                            }
                        }
                    }


                }

                launch {
                    viewModel.product.collect {
                        binding.data = it

                    }
                }

                launch {
                    viewModel.imageSlider.collect{
                        initSlider(it)
                    }
                }

            }
        }
    }

    private fun initSlider(data: List<ImageSlide>) {
        val imageList = ArrayList<SlideModel>()
        data.forEach {
            imageList.add(SlideModel(it.imageS))
        }
        binding.imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
    }


}