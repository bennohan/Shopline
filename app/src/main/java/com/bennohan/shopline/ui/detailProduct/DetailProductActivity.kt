package com.bennohan.shopline.ui.detailProduct

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
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
import com.bennohan.shopline.databinding.ItemDetailSizeBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.snacked
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DetailProductActivity :
    BaseActivity<ActivityDetailProductBinding, DetailProductViewModel>(R.layout.activity_detail_product) {


    var product: Product? = null
    private var listColor = ArrayList<Product.Variant?>()
    private var selectColor: Product.Variant? = null
    private var listSize = ArrayList<Product.Sizes?>()
    private var selectSize: Product.Sizes? = null

    private val adapterVariant by lazy {
        object : ReactiveListAdapter<ItemDetailBinding, Product.Variant>(R.layout.item_detail) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemDetailBinding, Product.Variant>,
                position: Int
            ) {
                //View Color
                listColor[position]?.let { data ->
                    holder.binding.data = data
                    holder.binding.cardView.setBackgroundColor(
                        if (data.selected) {
                            applicationContext.getColor(R.color.mainColor)
                        } else {
                            applicationContext.getColor(R.color.white)
                        }
                    )

                    //Data
                    holder.itemView.setOnClickListener {
                        listColor.forEachIndexed { index, variant ->
                            variant?.selected = index == position
                        }
                        notifyDataSetChanged()
                        selectColor = data
                        conditionForColor(data.id)
                        Timber.d("CekListColors: $listColor")
                        println("CekListColors: $listColor")
                    }
                }
            }
        }.initItem()
    }

    private val adapterSize by lazy {
        object :
            ReactiveListAdapter<ItemDetailSizeBinding, Product.Sizes>(R.layout.item_detail_size) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemDetailSizeBinding, Product.Sizes>,
                position: Int
            ) {
                listSize[position]?.let { data ->
                    holder.binding.data = data
                    holder.binding.cardView.setBackgroundColor(
                        if (data.selected) applicationContext.getColor(R.color.mainColor)
                        else applicationContext.getColor(R.color.white)
                    )
                    holder.itemView.setOnClickListener {
                        listSize.forEachIndexed { index, Size ->
                            Size?.selected = index == position
                        }
                        notifyDataSetChanged()
                        selectSize = data
                        Timber.d("CekListColors: $listSize")
                        println("CekListColors: $listSize")
                        if (data.selected) {
//                            binding.btnAddCart.setBackgroundColor(getResources().getColor(R.drawable.))
                            binding.btnAddCart.setBackgroundDrawable(getDrawable(R.drawable.button_border))

                        } else {
//                            binding.btnAddCart.setBackgroundColor(getResources().getColor(R.color.black))
                        }
                    }

                    Log.d("cek selected", "cek selected : ${data.selected}")


                }


            }

        }.initItem()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observe()

        productData()
        window.statusBarColor = ContextCompat.getColor(this,R.color.mainColor)


        binding.rvVariant.adapter = adapterVariant
        binding.rvSize.adapter = adapterSize


//        binding.imageSlider.setOnClickListener {
//            ImagePreviewHelper(this).show(binding.imageSlider, binding.user?.foto)
//        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAddCart.setOnClickListener {
            addCart()
        }


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
                            ApiStatus.LOADING -> {
                                loadingDialog.show()
                            }
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
//                                tos("Product Added to Cart")
                                binding.root.snacked("Product Added to Your Cart")
                                loadingDialog.setResponse(it.message ?: return@collect)
                            }
                            ApiStatus.ERROR -> {
                                loadingDialog.dismiss()
                                binding.root.snacked("Product Has Been In Your Cart")
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> {
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                        }

                    }
                }

                launch {
                    viewModel.imageSlider.collect {
                        initSlider(it)
                    }
                }
                launch {
                    viewModel.product.collect { product ->
                        binding.data = product
                        adapterVariant.submitList(product.variants)
                        listColor.clear()
                        product.variants?.let { list ->
                            listColor.addAll(list)
                        }

                        adapterSize.submitList(product.sizes)
                        listSize.clear()
                        product.sizes?.let { list ->
                            listSize.addAll(list)
                        }

                        product.imageSliders?.let { initSlider(it) }
                        println("ListVariant: ${product.variants}")
                        println("ListSizes: ${product.sizes}")
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

    private fun conditionForColor(idVarian: Int?) {
        if (selectColor == null) {
            binding.rvSize.visibility = View.INVISIBLE
        } else {

            binding.rvSize.visibility = View.VISIBLE
//            adapterColor.submitList(listColor)
            val filterSize = listSize.filter {
                it?.variantId == idVarian
            }
//            filterListSize.clear()
//            filterListSize.addAll(filterSize)
            adapterSize.submitList(filterSize)
        }
    }


    private fun addCart() {
        Log.d("cekSelectedAdd","cekSelectedAdd : ${selectSize?.selected}")
        if (selectSize?.selected == true) {
            selectSize?.let {
                viewModel.addCart(sizeId = selectSize?.id)

            }
        } else {
             binding.root.snacked("Pilih Variant dan Ukuran Terlebih dahulu")
        }
    }
}