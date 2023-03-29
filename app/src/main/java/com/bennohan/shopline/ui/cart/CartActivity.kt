package com.bennohan.shopline.ui.cart

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.shopline.R
import com.bennohan.shopline.base.BaseActivity
import com.bennohan.shopline.data.Product
import com.bennohan.shopline.databinding.ActivityCartBinding
import com.bennohan.shopline.databinding.ItemCartBinding
import com.bennohan.shopline.ui.checkout.CheckoutActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.tos
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartActivity : BaseActivity<ActivityCartBinding, CartViewModel>(R.layout.activity_cart) {


    private val adapterCart by lazy {
        object : ReactiveListAdapter<ItemCartBinding, Product>(R.layout.item_cart) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemCartBinding, Product>,
                position: Int
            ) {

                //Get Product
                val item = getItem(position)
                item?.let { itm ->
                    val productId = item.id
                    var totalProduct = item.totalProduct ?: 1
                    val totalProductPrice = item.totalCost
                    Log.d("cek id", "cek id : $productId ")
                    holder.binding.data = itm
                    holder.bind(itm)

                    //Button Delete
                    holder.binding.btnDelete.setOnClickListener {
                        binding.root.snacked("Product Removed")
                        productId?.let {
                            viewModel.deleteCart(id = it)
                        }
                    }

                    //Button Add
                    holder.binding.btnAdd.setOnClickListener {
                        binding.root.snacked("Add")
                        var pluss = itm.totalProduct ?: 1
                        if (totalProduct < 100) {
                            totalProduct++
                            viewModel.editCart(productId ?: 1, totalProduct)
                            holder.binding.etTotalProduct.setText(totalProduct.toString())
                        }
                    }

                    //Button Minus
                    holder.binding.btnRemove.setOnClickListener {
                        binding.root.snacked("Min")
//                        var min = itm.totalProduct ?: 1
                        if (totalProduct != 1) {
                            totalProduct--
                            viewModel.editCart(productId ?: 1, totalProduct)
                            holder.binding.etTotalProduct.text = totalProduct.toString()
                        }

                    }

                    //Text Data Kosong
                    if (item == null){
                        binding.btnCheckout.setOnClickListener {
//                            binding.root.snacked("Tambahkan Produk Dahulu")
                            tos("Tambahkan Produk Dahulu")
                        }
                        binding.tvkosong.visibility = View.VISIBLE
                    } else {
                        binding.btnCheckout.setOnClickListener {
                            openActivity<CheckoutActivity>()
                        }
                        binding.tvkosong.visibility = View.GONE
                    }

                }

                super.onBindViewHolder(holder, position)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getProduct()
        observe()

        binding.rvCart.adapter = adapterCart

        viewModel.getCart()



        binding.btnBack.setOnClickListener {
            onBackPressed()
        }


    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.SUCCESS -> {

                            }
                            else -> {

                            }
                        }
                    }
                }

                launch {
                    viewModel.responseProduct.collect { product ->
                        adapterCart.submitList(product)
                    }

                }

            }
        }
    }


    private fun getProduct() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

            }
        }
    }


}